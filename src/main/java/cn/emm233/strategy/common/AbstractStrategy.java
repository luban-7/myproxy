package cn.emm233.strategy.common;

import cn.emm233.strategy.StrategyContext;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/13 17:57
 */
@Slf4j
public abstract class AbstractStrategy {

    protected abstract void doHandler(StrategyContext context);

    public void exec(StrategyContext context) {
        try {
            this.doHandler(context);
        } catch (Exception e) {
            log.error("处理请求发生异常!context:{}", JSONUtil.toJsonStr(context), e);
        }
    }
}
