package cn.emm233.strategy.server;

import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/16 09:31
 */
@Slf4j
@Component("KEEPALIVE_TO_SERVER")
public class KeepAliveStrategy extends AbstractStrategy {


    /**
     * 处理存活命令
     *
     * @param context StrategyContext
     */
    @Override
    public void doHandler(StrategyContext context) {
        log.debug("[{}]存活", context.getMsg().getUserId());
    }
}
