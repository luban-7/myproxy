package cn.emm233.strategy.common;

import cn.emm233.strategy.StrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/14 17:06
 */
@Slf4j
@Component("EMPTY")
public class EmptyStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {
    }
}
