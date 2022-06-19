package cn.emm233.strategy.client;

import cn.emm233.common.codec.Message;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/18 16:07
 */
@Slf4j
@Component("SEND_MSG_TO_CLIENT")
public class SendMsgStrategy extends AbstractStrategy {
    @Override
    protected void doHandler(StrategyContext context) {
        Message msg = context.getMsg();
        if (StrUtil.isBlank(msg.getMsg())) {
            return;
        }
        log.info("MSG:[{}]", msg.getMsg());
    }
}
