package cn.emm233.common.handler;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：公共handler
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
@Slf4j
public class CommonHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常!ctx:[{}]", JSONUtil.toJsonStr(ctx), cause);
    }
}
