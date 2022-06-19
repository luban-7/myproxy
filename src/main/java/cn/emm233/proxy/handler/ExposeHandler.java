package cn.emm233.proxy.handler;

import cn.emm233.common.ExposConfig;
import cn.emm233.common.MessageType;
import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.tcp.TcpServer;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：暴露服务提供者handler
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
@Data
@Slf4j
public class ExposeHandler extends CommonHandler {

    private ProxyServerHandler proxyServerHandler;

    /**
     * 暴露服务
     */
    private TcpServer tcpServer;


    public ExposeHandler(TcpServer tcpServer, ProxyServerHandler proxyServerHandler) {
        this.tcpServer = tcpServer;
        this.proxyServerHandler = proxyServerHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            ExposConfig config = tcpServer.getExposConfig();

            Message message = new Message();
            message.setType(MessageType.REAL_CONNECTED_TO_CLIENT.getCode());
            message.setChannelId(ctx.channel().id().asLongText());
            message.setServerId(config.getServerId());
            message.setTargetHost(config.getTargetHost());
            message.setTargetPort(config.getTargetPort());
            proxyServerHandler.getCtx().writeAndFlush(message);
        } catch (Exception e) {
            log.error("Expose.channelRead发生异常!ctx:{}", ctx, e);
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            ExposConfig config = tcpServer.getExposConfig();

            Message message = new Message();
            message.setType(MessageType.REAL_DISCONNECTED_TO_CLIENT.getCode());
            message.setChannelId(ctx.channel().id().asLongText());
            message.setServerId(config.getServerId());
            message.setTargetHost(config.getTargetHost());
            message.setTargetPort(config.getTargetPort());
            proxyServerHandler.getCtx().writeAndFlush(message);
        } catch (Exception e) {
            log.error("Expose.channelInactive发生异常!ctx:{}", ctx, e);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ExposConfig config = tcpServer.getExposConfig();

            byte[] data = (byte[]) msg;
            Message message = new Message();
            message.setType(MessageType.REAL_DATA_TO_CLIENT.getCode());
            message.setData(data);
            message.setChannelId(ctx.channel().id().asLongText());
            message.setServerId(config.getServerId());
            message.setTargetHost(config.getTargetHost());
            message.setTargetPort(config.getTargetPort());
            proxyServerHandler.getCtx().writeAndFlush(message);
        } catch (Exception e) {
            log.error("Expose.channelRead发生异常!ctx:{}", ctx, e);
        }
    }
}
