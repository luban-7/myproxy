package cn.emm233;

import cn.emm233.config.AppProps;
import cn.emm233.config.ProviderProps;
import cn.emm233.config.ProxyProps;
import cn.emm233.enums.RoleEnum;
import cn.emm233.provider.Provider;
import cn.emm233.proxy.ProxyServer;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 描述：启动类
 *
 * @author zhangchong
 * @date 2022/5/10 20:42
 */
@Slf4j
@Component
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private AppProps appProps;

    @Resource
    private ProxyProps proxyProps;

    @Resource
    private ProviderProps providerProps;

    @Resource
    private ProxyServer proxyServer;

    @Resource
    private Provider provider;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(Application.class).build(args);
        application.addListeners(new ApplicationPidFileWriter());
        application.run();
    }

    @Override
    public void run(String... args) throws Exception {
        if (Objects.nonNull(appProps) && StrUtil.equals(appProps.getRole(), RoleEnum.PROXY.getRole())) {
            this.checkProxyProps();
            proxyServer.run();
        } else {
            provider.run();
        }
    }

    /**
     * 校验配置文件
     */
    private void checkProxyProps() throws Exception {

        if (Objects.isNull(proxyProps) || StrUtil.isBlank(proxyProps.getId())) {
            log.error("配置异常!id不能为空");
            throw new Exception("配置异常!id不能为空");
        }
    }
}
