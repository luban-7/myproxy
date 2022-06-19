package cn.emm233.strategy.client;

import cn.emm233.common.codec.Message;
import cn.emm233.provider.handler.ProxyClientHandler;
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
@Component("REGISTER_RESULT_TO_CLIENT")
public class RegisterResultStrategy extends AbstractStrategy {
    @Override
    protected void doHandler(StrategyContext context) {
        ProxyClientHandler clientHandler = context.getClientHandler();
        Message msg = context.getMsg();
        if (StrUtil.isNotBlank(msg.getMsg())) {
            log.info("注册失败,请重试!失败原因:[{}]", msg.getMsg());
            clientHandler.getCtx().close();
        } else {
            log.info("注册成功!请到服务平台配置映射信息");
        }

    }
}
