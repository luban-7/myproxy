package cn.emm233.strategy.common;

import cn.emm233.strategy.StrategyContext;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 描述：默认策略
 *
 * @author zhangchong
 * @date 2022/5/14 17:06
 */
@Slf4j
@Component
public class DefaultStrategy extends AbstractStrategy {

    @Override
    public void doHandler(StrategyContext context) {
        log.info("请求无法处理:{}", JSONUtil.toJsonStr(context));
    }
}
