package cn.emm233.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述：提供服务方配置
 *
 * @author zhangchong
 * @date 2022/5/10 20:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "provider")
public class ProviderProps {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 代理host
     */
    private String proxyHost = "127.0.0.1";

    /**
     * 代理port
     */
    private String proxyPort = "9494";


}