package cn.emm233.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述：代理服务端配置
 *
 * @author zhangchong
 * @date 2022/5/10 20:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProps {

    /**
     * 服务端id
     */
    private String id;

    /**
     * 代理暴露端口
     */
    private String proxyPort = "9494";

    /**
     * 从redis更细config的任务间隔时间，单位秒
     */
    private Integer allUpdateSpan = 5;

    /**
     * 每个客户端检查config任务间隔时间，单位秒
     */
    private Integer serverUpdateSpan = 5;
}