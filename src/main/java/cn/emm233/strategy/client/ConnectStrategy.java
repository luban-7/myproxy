package cn.emm233.strategy.client;

import cn.emm233.common.MessageType;
import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.provider.handler.ProviderHandler;
import cn.emm233.provider.handler.ProxyClientHandler;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.emm233.tcp.TcpConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/18 16:07
 */
@Slf4j
@Component("REAL_CONNECTED_TO_CLIENT")
public class ConnectStrategy extends AbstractStrategy {
    @Override
    protected void doHandler(StrategyContext context) {
        ProxyClientHandler clientHandler = context.getClientHandler();
        ConcurrentHashMap<String, CommonHandler> channelHandlerMap = clientHandler.getChannelHandlerMap();
        ChannelGroup channelGroup = ProxyClientHandler.channelGroup;
        ChannelHandlerContext ctx = clientHandler.getCtx();
        Message msg = context.getMsg();
        try {

            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(msg.getTargetHost(), msg.getTargetPort(), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ProviderHandler providerHandler = new ProviderHandler(clientHandler, msg.getChannelId(), msg.getServerId());
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), providerHandler);
                    channelHandlerMap.put(msg.getChannelId(), providerHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            Message message = new Message();
            message.setType(MessageType.REAL_DISCONNECTED_TO_SERVER.getCode());
            message.setChannelId(msg.getChannelId());
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(msg.getChannelId());
            log.error("建立目标端口失败!context:{}", context, e);
        }
    }
}
