package cn.emm233.strategy.client;

import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/16 09:31
 */
@Component("REAL_DATA_TO_CLIENT")
public class DataStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {
        Message msg = context.getMsg();
        ConcurrentHashMap<String, CommonHandler> channelHandlerMap = context.getClientHandler().getChannelHandlerMap();

        String channelId = msg.getChannelId();
        CommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(msg.getData());
        }
    }
}
