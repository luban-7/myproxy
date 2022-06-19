package cn.emm233.config;

import cn.emm233.enums.RoleEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述：公共配置
 *
 * @author zhangchong
 * @date 2022/5/10 20:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProps {

    /**
     * 运行角色
     */
    private String role = RoleEnum.PROXY.getRole();
}