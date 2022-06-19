package cn.emm233.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ProxyServer与ProxyClient交互命令
 * 大于1服务端发给客户端，小于1客户端发给服务端
 *
 * @author zhangchong
 * @date 2019/3/2
 */
@Getter
@AllArgsConstructor
public enum MessageType {

    /**
     * 客户端注册
     */
    REGISTER_TO_SERVER(0, "REGISTER_TO_SERVER"),

    /**
     * 心跳命令，由客户端发送给服务端
     */
    KEEPALIVE_TO_SERVER(-1, "KEEPALIVE_TO_SERVER"),
    KEEPALIVE_TO_CLIENT(1, "EMPTY"),
    /**
     * 连接请求
     */
    REAL_CONNECTED_TO_CLIENT(2, "REAL_CONNECTED_TO_CLIENT"),
    REAL_CONNECTED_TO_SERVER(-2, ""),
    /**
     * 断开连接
     */
    REAL_DISCONNECTED_TO_CLIENT(3, "REAL_DISCONNECTED_TO_CLIENT"),
    REAL_DISCONNECTED_TO_SERVER(-3, "REAL_DISCONNECTED_TO_SERVER"),
    /**
     * 数据
     */
    REAL_DATA_TO_CLIENT(4, "REAL_DATA_TO_CLIENT"),
    REAL_DATA_TO_SERVER(-4, "REAL_DATA_TO_SERVER"),

    /**
     * 注册结果
     */
    REGISTER_RESULT_TO_CLIENT(5, "REGISTER_RESULT_TO_CLIENT"),
    SEND_MSG_TO_CLIENT(6, "SEND_MSG_TO_CLIENT"),

    ;

    /**
     * 命令编码
     */
    private int code;


    /**
     * 命令Bean Name
     */
    private String commandName;


    public static MessageType valueOf(int code) {
        for (MessageType item : MessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }

}
