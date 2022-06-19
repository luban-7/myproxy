package cn.emm233.strategy.server;

import cn.emm233.common.codec.Message;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.emm233.tcp.TcpServer;
import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.group.ChannelGroup;
import org.springframework.stereotype.Component;

/**
 * 描述：断开连接
 *
 * @author zhangchong
 * @date 2022/5/16 09:32
 */
@Component("REAL_DISCONNECTED_TO_SERVER")
public class DisConnectStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {

        if (CollectionUtil.isEmpty(context.getServerHandler().getExposeServerMap())) {
            return;
        }
        Message msg = context.getMsg();
        TcpServer tcpServer = context.getServerHandler().getExposeServerMap().get(msg.getServerId());
        ChannelGroup channelGroup = tcpServer.getChannelGroup();
        channelGroup.close(channel -> channel.id().asLongText().equals(msg.getChannelId()));
    }
}
