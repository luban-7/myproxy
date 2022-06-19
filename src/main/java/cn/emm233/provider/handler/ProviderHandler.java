package cn.emm233.provider.handler;

import cn.emm233.common.MessageType;
import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 描述：服务提供者handler
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
public class ProviderHandler extends CommonHandler {

    private CommonHandler proxyHandler;
    private String remoteChannelId;

    private String serverId;

    public ProviderHandler(CommonHandler proxyHandler, String remoteChannelId, String serverId) {
        this.proxyHandler = proxyHandler;
        this.remoteChannelId = remoteChannelId;
        this.serverId = serverId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        Message message = new Message();
        message.setType(MessageType.REAL_DATA_TO_SERVER.getCode());
        message.setData(data);
        message.setChannelId(remoteChannelId);
        message.setServerId(serverId);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        message.setType(MessageType.REAL_DISCONNECTED_TO_SERVER.getCode());
        message.setChannelId(this.remoteChannelId);
        message.setServerId(this.serverId);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}
