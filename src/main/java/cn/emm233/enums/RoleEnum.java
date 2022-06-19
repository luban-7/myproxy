package cn.emm233.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：角色枚举
 *
 * @author zhangchong
 * @date 2022/5/10 21:26
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 代理角色
     */
    PROXY("proxy"),
    /**
     * 提供者角色
     */
    PROVIDER("provider"),
    ;

    private String role;
}
