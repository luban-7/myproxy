package cn.emm233.strategy.server;

import cn.emm233.common.RedisKeyConstant;
import cn.emm233.common.RedisUtils;
import cn.emm233.common.codec.Message;
import cn.emm233.proxy.handler.ProxyServerHandler;
import cn.emm233.strategy.StrategyContext;
import cn.emm233.strategy.common.AbstractStrategy;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static cn.emm233.common.MessageType.REGISTER_RESULT_TO_CLIENT;

/**
 * 描述：
 *
 * @author zhangchong
 * @date 2022/5/16 09:31
 */
@Slf4j
@Component("REGISTER_TO_SERVER")
public class RegisterStrategy extends AbstractStrategy {


    /**
     * 处理注册
     *
     * @param context StrategyContext
     */
    @Override
    public void doHandler(StrategyContext context) {
        Message msg = context.getMsg();
        ProxyServerHandler serverHandler = context.getServerHandler();
        String userId = msg.getUserId();
        if (StrUtil.isBlank(userId)) {
            log.info("用户的userId不合法:[{}]", userId);
            return;
        }
        String userIdKey = RedisKeyConstant.getUserIdKey(userId);

        Boolean result;
        String reason = "";
        try {
            result = RedisUtils.setNx(userIdKey, "");
            RedisUtils.setTime(userIdKey, 40, TimeUnit.SECONDS);

        } catch (Exception e) {
            RedisUtils.delete(userIdKey);
            result = false;
            reason = "系统异常";
            log.error("注册失败:[{}],redis操作异常", userId, e);
        }

        if (Boolean.TRUE.equals(result)) {
            serverHandler.setUserId(userId);
            serverHandler.aliveSchedule(context.getServerHandler().getCtx().channel());
            log.info("[{}]注册成功", userId);

            Message message = new Message();
            message.setType(REGISTER_RESULT_TO_CLIENT.getCode());
            this.write(context, message);
            return;
        }

        if (StrUtil.isBlank(reason)) {
            reason = "userId已被使用";
            log.info("注册失败:[{}],userId已被使用", userId);
        }

        // 返回失败结果
        Message message = new Message();
        message.setType(REGISTER_RESULT_TO_CLIENT.getCode());
        message.setMsg(reason);
        this.write(context, message);
    }


    /**
     * 返回给客户端消息
     *
     * @param context context
     * @param message message
     */
    private void write(StrategyContext context, Message message) {
        ChannelHandlerContext ctx = context.getServerHandler().getCtx();
        ctx.writeAndFlush(message);
    }
}
