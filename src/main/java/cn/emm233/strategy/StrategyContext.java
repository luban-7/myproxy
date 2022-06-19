package cn.emm233.strategy;

import cn.emm233.common.codec.Message;
import cn.emm233.provider.handler.ProxyClientHandler;
import cn.emm233.proxy.handler.ProxyServerHandler;
import lombok.Builder;
import lombok.Data;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/14 17:11
 */
@Data
@Builder
public class StrategyContext {

    /**
     * 代理原始消息
     */
    private Message msg;

    /**
     * ProxyServerHandler
     */
    private ProxyServerHandler serverHandler;

    /**
     * ProxyClientHandler
     */
    private ProxyClientHandler clientHandler;

}
