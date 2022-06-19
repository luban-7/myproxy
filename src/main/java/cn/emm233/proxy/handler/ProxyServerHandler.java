package cn.emm233.proxy.handler;

import cn.emm233.common.ExposConfig;
import cn.emm233.common.MessageType;
import cn.emm233.common.RedisKeyConstant;
import cn.emm233.common.RedisUtils;
import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.config.ProxyProps;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.StrategyFactory;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.emm233.tcp.TcpServer;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.emm233.common.RedisKeyConstant.getUserIdKey;

/**
 * 描述：代理服务端Handler
 *
 * @author zhangchong
 * @date 2022/5/10 21:51
 */
@Slf4j
@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyServerHandler extends CommonHandler {

    /**
     * 持有并管理实际暴露端口的服务
     */
    private Map<String, TcpServer> exposeServerMap = new ConcurrentHashMap<>();

    private List<ExposConfig> configs;

    /**
     * proxyClient对应的Client
     */
    private volatile String userId;

    /**
     * 定时任务
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 更新redis过期时间的Scheduled
     */
    private ScheduledFuture<?> aliveScheduledFuture;

    @Resource
    private StrategyFactory strategyFactory;

    @Resource
    private ProxyProps proxyProps;

    @Resource
    private ProxyProps props;

    public ProxyServerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelActive(ctx);
            this.updateConfigSchedule(ctx.channel());
        } catch (Exception e) {
            log.error("ProxyServer.channelActive发生异常!ctx:{}", ctx, e);
        }

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {
            Message message = (Message) msg;
            AbstractStrategy strategy = strategyFactory.getStrategy(message);
            StrategyContext context = StrategyContext.builder()
                    .msg(message)
                    .serverHandler(this)
                    .build();

            strategy.exec(context);
        } catch (Exception e) {
            log.error("ProxyServer.channelRead发生异常!ctx:{},msg:{}", ctx, msg, e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            // 移除userId在redis中的缓存
            if (StrUtil.isNotBlank(userId)) {
                RedisUtils.delete(getUserIdKey(userId));
            }

            // 关闭定时任务
            if (Objects.nonNull(this.scheduledFuture)) {
                this.scheduledFuture.cancel(true);
            }

            // 关闭redis定时任务
            if (Objects.nonNull(this.aliveScheduledFuture)) {
                this.aliveScheduledFuture.cancel(true);
            }
            // 关闭监听服务
            exposeServerMap.values().forEach(TcpServer::close);
            log.info("停止代理服务:{}", JSONUtil.toJsonStr(exposeServerMap));
        } catch (Exception e) {
            log.error("ProxyServer.channelInactive发生异常!ctx:{}", ctx, e);
        }
    }

    /**
     * 心跳处理
     *
     * @param ctx ChannelHandlerContext
     * @param evt Object
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                log.info("客户端无响应,连接断开,userId:{}", userId);
                System.out.println("Read idle loss connection.");
                ctx.close();
            }else if (e.state() == IdleState.WRITER_IDLE) {
                Message message = new Message();
                message.setType(MessageType.KEEPALIVE_TO_CLIENT.getCode());
                ctx.writeAndFlush(message);
            }
        }
    }

    /**
     * 开启更新暴露服务定时任务
     *
     * @param channel 定时任务关联的channel
     */
    private void updateConfigSchedule(Channel channel) {

        this.scheduledFuture = channel.eventLoop().scheduleWithFixedDelay(() -> {
            try {
                if (StrUtil.isBlank(userId)) {
                    return;
                }

                // 获取redis中的配置
                String configStr = (String) RedisUtils.getHash(RedisKeyConstant.CONFIG_MAP, userId);
                List<ExposConfig> configs = JSONUtil.toList(configStr, ExposConfig.class);
                this.configs = configs;
                if (CollUtil.isEmpty(configs)) {
                    this.initConfig(userId);
                }

                // 过滤disable为true的
                configs = configs.stream().filter(config -> !config.getDisable()).collect(Collectors.toList());

                Set<String> existedServerIds = exposeServerMap.keySet();
                List<String> serverIds = configs.stream()
                        .map(ExposConfig::getServerId)
                        .collect(Collectors.toList());

                // 过滤重复的serverId
                Map<String, List<String>> serverIdMap = serverIds.stream()
                        .collect(Collectors.groupingBy(Function.identity()));
                List<String> repeatIds = Lists.newArrayList();
                for (Map.Entry<String, List<String>> entry : serverIdMap.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        repeatIds.add(entry.getKey());
                    }
                }
                if (CollectionUtil.isNotEmpty(repeatIds)) {
                    sendMsgToClient(StrUtil.format("用户[{}],serverId配置重复:[{}]", userId,
                            String.join(",", repeatIds)), null);

                }
                serverIds = serverIds.stream().filter(id -> !repeatIds.contains(id)).collect(Collectors.toList());
                List<ExposConfig> exposConfigs = configs.stream()
                        .filter(config -> !repeatIds.contains(config.getServerId()))
                        .collect(Collectors.toList());


                // 需要停止服务的serverId
                List<String> finalServerIds = serverIds;
                List<String> deleteIds = existedServerIds.stream()
                        .filter(id -> !finalServerIds.contains(id))
                        .collect(Collectors.toList());

                this.deleteExposeServer(deleteIds);
                this.addExposeServer(exposConfigs);
            } catch (Exception e) {
                log.error("用户[{}],更新暴露服务失败,configs:{}", userId, configs, e);
            }
        }, 1, proxyProps.getServerUpdateSpan(), TimeUnit.SECONDS);
    }

    /**
     * 初始化配置
     *
     * @param userId 用户id
     */
    private void initConfig(String userId) {
        if (StrUtil.isBlank(userId)) {
            return;
        }

        ExposConfig exposConfig = ExposConfig.builder()
                .targetHost("localhost")
                .targetPort(0)
                .exposePort(RandomUtil.randomInt(32768, 65535))
                .serverId("默认服务")
                .disable(false)
                .build();

        RedisUtils.putHash(RedisKeyConstant.CONFIG_MAP, userId, JSONUtil.toJsonStr(Lists.newArrayList(exposConfig)));
    }

    /**
     * 新增暴露服务
     *
     * @param exposConfigs exposConfigs
     */
    private void addExposeServer(List<ExposConfig> exposConfigs) {
        if (CollectionUtil.isEmpty(exposConfigs)) {
            return;
        }

        for (ExposConfig config : exposConfigs) {
            TcpServer tcpServer = exposeServerMap.get(config.getServerId());
            if (Objects.isNull(tcpServer)) {
                this.exposeServer(config);
                continue;
            }

            if (Objects.equals(tcpServer.getExposConfig(), config)) {
                continue;
            }

            this.closeExposeServer(tcpServer);
            this.exposeServer(config);
        }


    }

    /**
     * 停止暴露服务
     *
     * @param deleteIds serverId
     */
    private void deleteExposeServer(List<String> deleteIds) {
        if (CollectionUtil.isEmpty(deleteIds)) {
            return;
        }
        Set<Map.Entry<String, TcpServer>> entries = exposeServerMap.entrySet();
        Iterator<Map.Entry<String, TcpServer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TcpServer> entry = iterator.next();
            if (!deleteIds.contains(entry.getKey())) {
                continue;
            }
            this.closeExposeServer(entry.getValue());
            iterator.remove();
        }
    }

    /**
     * 关闭暴露服务
     *
     * @param server server
     */
    private void closeExposeServer(TcpServer server) {
        try {
            server.close();
            sendMsgToClient(StrUtil.format("用户[{}],停止服务成功,exposConfig:{}",
                    userId, server.getExposConfig()), null);
        } catch (Exception e) {
            sendMsgToClient(StrUtil.format("用户[{}],停止服务失败,exposConfig:{}",
                    userId, server.getExposConfig()), e);
        }
    }

    /**
     * 暴露服务
     *
     * @param exposConfig 暴露服务设置
     */
    private void exposeServer(ExposConfig exposConfig) {

        String serverId = exposConfig.getServerId();
        try {
            DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            TcpServer exposeServer = new TcpServer(channelGroup, exposConfig);
            exposeServerMap.put(serverId, exposeServer);
            ProxyServerHandler handler = this;
            exposeServer.bind(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ByteArrayDecoder(),
                            new ByteArrayEncoder(),
                            new ExposeHandler(exposeServer, handler));

                    channelGroup.add(ch);
                }
            });
            sendMsgToClient(StrUtil.format("用户[{}],暴露服务成功,exposConfig:{}", userId, exposConfig), null);
        } catch (Exception e) {
            sendMsgToClient(StrUtil.format("用户[{}],暴露服务失败,exposConfig:{}", userId, exposConfig), e);
        }

    }


    /**
     * 开启更新redis过期时间的定时任务
     *
     * @param channel channel
     */
    public void aliveSchedule(Channel channel) {
        this.aliveScheduledFuture = channel.eventLoop().scheduleAtFixedRate(() -> {
            try {
                if (StrUtil.isBlank(userId)) {
                    return;
                }
                RedisUtils.set(getUserIdKey(userId), "", 40, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("用户[{}],更新redis超时时间失败", userId, e);
            }
        }, 1, 30, TimeUnit.SECONDS);
    }

    /**
     * 发送MSG到客户端
     *
     * @param msg 内容
     * @param e   异常
     */
    private void sendMsgToClient(String msg, Exception e) {
        if (StrUtil.isBlank(msg)) {
            return;
        }
        if (Objects.isNull(e)) {
            log.info(msg);
        } else {
            log.error(msg, e);
        }

        Message message = new Message();
        message.setType(MessageType.SEND_MSG_TO_CLIENT.getCode());
        message.setMsg(msg);

        ctx.writeAndFlush(message);
    }

}
