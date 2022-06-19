package cn.emm233.common.codec;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：服务配置
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
@Data
public class Message {

    private Integer type;
    private Map<String, Object> metaData = new HashMap<>();
    private byte[] data;


    /**
     * 获取 ChannelId
     *
     * @return ChannelId
     */
    public String getChannelId() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (String) metaData.get(MessageConstant.CHANNEL_ID);
    }

    /**
     * 设置ChannelId
     *
     * @param channelId ChannelId
     */
    public void setChannelId(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            return;
        }
        metaData.put(MessageConstant.CHANNEL_ID, channelId);
    }

    /**
     * 获取serverId
     *
     * @return serverId
     */
    public String getServerId() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (String) metaData.get(MessageConstant.SERVER_ID);
    }

    /**
     * 设置serverId
     *
     * @param serverId serverId
     */
    public void setServerId(String serverId) {
        if (StrUtil.isBlank(serverId)) {
            return;
        }
        metaData.put(MessageConstant.SERVER_ID, serverId);
    }

    /**
     * 获取目标主机
     *
     * @return 目标主机
     */
    public String getTargetHost() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (String) metaData.get(MessageConstant.TARGET_HOST);
    }

    /**
     * 设置目标主机
     *
     * @param targetHost 目标主机
     */
    public void setTargetHost(String targetHost) {
        if (StrUtil.isBlank(targetHost)) {
            return;
        }
        metaData.put(MessageConstant.TARGET_HOST, targetHost);
    }

    /**
     * 获取目标端口
     *
     * @return 目标端口
     */
    public Integer getTargetPort() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (Integer) metaData.get(MessageConstant.TARGET_PORT);
    }

    /**
     * 设置目标端口
     *
     * @param targetPort 目标端口
     */
    public void setTargetPort(Integer targetPort) {
        if (Objects.isNull(targetPort)) {
            return;
        }
        metaData.put(MessageConstant.TARGET_PORT, targetPort);
    }

    /**
     * 取userId
     *
     * @return userId
     */
    public String getUserId() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (String) metaData.get(MessageConstant.USER_ID);
    }

    /**
     * 设置userId
     *
     * @param userId userId
     */
    public void setUserId(String userId) {
        if (StrUtil.isBlank(userId)) {
            return;
        }
        metaData.put(MessageConstant.USER_ID, userId);
    }

    /**
     * 取msg
     *
     * @return userId
     */
    public String getMsg() {
        if (CollectionUtil.isEmpty(metaData)) {
            return null;
        }
        return (String) metaData.get(MessageConstant.MSG);
    }

    /**
     * 设置msg
     *
     * @param msg msg
     */
    public void setMsg(String msg) {
        if (StrUtil.isBlank(msg)) {
            return;
        }
        metaData.put(MessageConstant.MSG, msg);
    }


    private static class MessageConstant {
        private final static String CHANNEL_ID = "channelId";
        private final static String SERVER_ID = "serverId";

        private final static String TARGET_HOST = "targetHost";
        private final static String TARGET_PORT = "targetPort";

        private final static String USER_ID = "userId";

        private final static String MSG = "msg";

    }
}
