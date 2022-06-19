package cn.emm233.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description: redis工具类
 * @ClassName: RedisHelper
 * @Date: 2019/12/3 16:43
 */
@Component
public class RedisUtils {
    @Autowired
    public StringRedisTemplate stringRedisTemplate1;

    private static StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        stringRedisTemplate = stringRedisTemplate1;
    }

    public static String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 存入Hash类型
     */
    public static void putHash(Object h, Object hk, Object hv) {
        stringRedisTemplate.opsForHash().put(h.toString(), hk.toString(), hv.toString());
    }

    public static void setHashTime(Object key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key.toString(), timeout, unit);
    }

    /**
     * 获得hash类型的value值
     */
    public static Object getHash(Object h, Object hk) {
        return stringRedisTemplate.opsForHash().get(h.toString(), hk);
    }

    /**
     * 获得所有value值
     */
    public static List getHashValues(Object h) {
        return stringRedisTemplate.opsForHash().values(h.toString());
    }

    /**
     * 获得hash类型的所有key值
     */
    public static Set<Object> getHashKeys(Object h) {
        return stringRedisTemplate.opsForHash().keys(h.toString());
    }

    /**
     * 设置有效时间
     */
    public static void setTime(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 默认时间一天
     *
     * @param key
     * @param data
     */
    public static void set(String key, String data) {
        stringRedisTemplate.opsForValue().set(key, data, 60 * 60 * 24, TimeUnit.SECONDS);
    }

    /**
     * 自定义时间
     *
     * @param key
     * @param data
     */
    public static void set(String key, String data, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, data, timeout, unit);
    }

    public static Boolean setNx(String key, String data) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, data);
    }

    /**
     * 根据Key删除缓存
     *
     * @param key
     */
    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据key删除hash的值
     *
     * @param key
     * @param sn
     */
    public static void deleteHash(String key, String sn) {
        stringRedisTemplate.opsForHash().delete(key, sn);
    }

    /**
     * 获取
     *
     * @param prefix uc-
     * @param id     222
     * @param key    feafds
     * @return
     */
    public static String getMatch(String prefix, Long id, String key) {
        String matchkey = "[prefix:" + prefix + id + "]" + key;
        return stringRedisTemplate.opsForValue().get(matchkey);
    }

    /**
     * 存储REDIS队列 顺序存储
     *
     * @param key  reids键名
     * @param data 数据
     */
    public static void rpush(String key, String data) {
        stringRedisTemplate.opsForList().rightPush(key, data);
    }

    /**
     * 获取队列数据
     *
     * @param key reids键名
     */
    public static String lpop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * @Description: 添加set集合
     * @Param: [key, data]
     * @Author: liangbl
     * @Date: 2018/10/22
     */
    public static void setAdd(String key, String data) {
        stringRedisTemplate.opsForSet().add(key, data);
    }

    /**
     * @Description: 移除set集合中一个或多个成员
     * @Param: [key, values]
     * @Author: liangbl
     * @Date: 2018/10/22
     */
    public static void setRemove(String key, Object... values) {
        stringRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * @Description: 返回set集合中的所有成员
     * @Param: [key]
     * @Author: liangbl
     * @Date: 2018/10/22
     */
    public static Set<String> setMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 计数器 val自增自减
     *
     * @param key
     * @param val eg:1加   -1减
     */
    public static void incre(String key, long val) {
        stringRedisTemplate.boundValueOps(key).increment(val);
    }

    /**
     * hash计数器 val自增自减
     *
     * @param key
     * @param hk
     * @param val eg:1加   -1减
     */
    public static void hashIncre(String key, String hk, long val) {
        stringRedisTemplate.opsForHash().increment(key, hk, val);
    }

    public static void pushMsg(String key, String msg) {
        stringRedisTemplate.convertAndSend(key, msg);
    }
}