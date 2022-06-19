package cn.emm233.provider.handler;

import cn.emm233.common.MessageType;
import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.config.ProviderProps;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.StrategyFactory;
import cn.emm233.strategy.common.AbstractStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：代理客户端handler
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
@Getter
@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyClientHandler extends CommonHandler {

    @Resource
    private StrategyFactory strategyFactory;

    @Resource
    private ProviderProps providerProps;

    private ConcurrentHashMap<String, CommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Message message = new Message();
        message.setType(MessageType.REGISTER_TO_SERVER.getCode());
        message.setUserId(providerProps.getUserId());
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            Message message = (Message) msg;
            AbstractStrategy strategy = strategyFactory.getStrategy(message);
            StrategyContext context = StrategyContext.builder()
                    .msg(message)
                    .clientHandler(this)
                    .build();

            strategy.exec(context);
        } catch (Exception e) {
            log.error("ProxyClient.channelRead发生异常!ctx:{},msg:{}", ctx, msg, e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        log.info("服务端已关闭,请重试");
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
                log.info("服务端无响应,关闭服务");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                Message message = new Message();
                message.setType(MessageType.KEEPALIVE_TO_SERVER.getCode());
                message.setUserId(providerProps.getUserId());
                ctx.writeAndFlush(message);
            }
        }
    }
}
