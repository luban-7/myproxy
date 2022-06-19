package cn.emm233.strategy.server;

import cn.emm233.common.codec.Message;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.emm233.tcp.TcpServer;
import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.group.ChannelGroup;
import org.springframework.stereotype.Component;

/**
 * 描述：转发数据
 *
 * @author zhangchong
 * @date 2022/5/16 09:31
 */
@Component("REAL_DATA_TO_SERVER")
public class DataStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {
        if (CollectionUtil.isEmpty(context.getServerHandler().getExposeServerMap())) {
            return;
        }
        Message msg = context.getMsg();
        TcpServer tcpServer = context.getServerHandler().getExposeServerMap().get(msg.getServerId());
        ChannelGroup channelGroup = tcpServer.getChannelGroup();
        channelGroup.writeAndFlush(msg.getData(), channel -> channel.id().asLongText().equals(msg.getChannelId()));
    }
}
