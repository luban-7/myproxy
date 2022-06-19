package cn.emm233.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 描述：服务配置
 *
 * @author zhangchong
 * @date 2022/5/20 11:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExposConfig {

    /**
     * 暴露服务标志
     */
    private String serverId;

    /**
     * 目标主机
     */
    private String targetHost;

    /**
     * 目标端口
     */
    private Integer targetPort;

    /**
     * 暴露的端口
     */
    private Integer exposePort;

    /**
     * 是否禁用
     */
    private Boolean disable;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExposConfig config = (ExposConfig) o;
        return serverId.equals(config.serverId) && targetHost.equals(config.targetHost) && targetPort.equals(config.targetPort) && exposePort.equals(config.exposePort) && disable.equals(config.disable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, targetHost, targetPort, exposePort, disable);
    }
}
