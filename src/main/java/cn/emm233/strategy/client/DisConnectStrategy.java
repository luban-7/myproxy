package cn.emm233.strategy.client;

import cn.emm233.common.codec.Message;
import cn.emm233.common.handler.CommonHandler;
import cn.emm233.provider.handler.ProxyClientHandler;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：断开连接
 *
 * @author zhangchong
 * @date 2022/5/16 09:32
 */
@Component("REAL_DISCONNECTED_TO_CLIENT")
public class DisConnectStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {
        Message msg = context.getMsg();
        ProxyClientHandler clientHandler = context.getClientHandler();
        ConcurrentHashMap<String, CommonHandler> channelHandlerMap = clientHandler.getChannelHandlerMap();

        String channelId = msg.getChannelId();
        CommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }
}
