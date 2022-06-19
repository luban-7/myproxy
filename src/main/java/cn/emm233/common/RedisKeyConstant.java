package cn.emm233.common;

import cn.emm233.config.ProxyProps;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 描述：rediskey
 *
 * @author zhangchong
 * @date 2022/5/25 20:44
 */
@Component
public class RedisKeyConstant {

    @Resource
    private ProxyProps proxyProps;

    /**
     * proxy_client id集合
     */
    public static String USER_ID = "{}:userId:{}";

    /**
     * 配置map，key为userId
     */
    public static String CONFIG_MAP = "{}:configMap";

    @PostConstruct
    public void init() {
        USER_ID = StrUtil.format(USER_ID, proxyProps.getId());
        CONFIG_MAP = StrUtil.format(CONFIG_MAP, proxyProps.getId());
    }

    public static String getUserIdKey(String userId) {
        if (StrUtil.isBlank(userId)) {
            return null;
        }

        return StrUtil.format(USER_ID, userId);

    }
}
