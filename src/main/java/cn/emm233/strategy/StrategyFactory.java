package cn.emm233.strategy;

import cn.emm233.common.MessageType;
import cn.emm233.common.codec.Message;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.emm233.strategy.common.DefaultStrategy;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：命令工厂
 *
 * @author zhangchong
 * @date 2022/5/14 16:25
 */
@Component
public class StrategyFactory {

    @Resource
    private Map<String, AbstractStrategy> commandMap;

    /**
     * 根据message获取命令
     *
     * @param message message
     * @return AbstractCommand
     */
    public AbstractStrategy getStrategy(Message message) {
        if (Objects.isNull(message) || Objects.isNull(message.getType())) {
            return new DefaultStrategy();
        }
        return this.getStrategy(message.getType());
    }

    /**
     * 根据code获取命令
     *
     * @param code code
     * @return AbstractCommand
     */
    public AbstractStrategy getStrategy(Integer code) {
        if (Objects.isNull(code)) {
            return new DefaultStrategy();
        }
        MessageType messageType = MessageType.valueOf(code);
        if (Objects.isNull(messageType)) {
            return new DefaultStrategy();
        }
        return this.getStrategy(messageType.getCommandName());
    }

    /**
     * 根据BeanName获取命令
     *
     * @param commandName BeanName
     * @return AbstractCommand
     */
    public AbstractStrategy getStrategy(String commandName) {
        if (StrUtil.isBlank(commandName)) {
            return new DefaultStrategy();
        }
        AbstractStrategy strategy = commandMap.get(commandName);
        return Objects.isNull(strategy) ? new DefaultStrategy() : strategy;
    }
}
