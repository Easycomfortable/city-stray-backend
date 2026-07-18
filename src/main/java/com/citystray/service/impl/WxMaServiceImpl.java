package com.citystray.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.config.WxMaProperties;
import com.citystray.entity.User;
import com.citystray.mapper.UserMapper;
import com.citystray.service.WxMaService;
import com.citystray.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxMaServiceImpl implements WxMaService {

    private final WxMaProperties wxMaProperties;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    /** 微信 code2session 接口地址 */
    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    @Override
    public Map<String, Object> wxLogin(String code) {
        // 检查是否为真实appid，否则使用mock登录（毕设演示用）
        String appid = wxMaProperties.getAppid();
        boolean isRealAppid = appid != null && !appid.isEmpty()
                && !appid.contains("your_appid");

        if (!isRealAppid) {
            log.info("使用Mock登录模式（未配置真实appid）");
            return mockLogin(code);
        }

        // 1. 调用微信 code2session 接口
        String url = CODE2SESSION_URL
                .replace("{appid}", appid)
                .replace("{secret}", wxMaProperties.getSecret())
                .replace("{code}", code);

        String response = HttpUtil.get(url);
        log.info("微信code2session响应: {}", response);

        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            log.warn("微信登录失败，回退到Mock模式: {}", json.getStr("errmsg"));
            return mockLogin(code);
        }

        String openid = json.getStr("openid");
        String sessionKey = json.getStr("session_key");

        if (openid == null || openid.isEmpty()) {
            log.warn("未获取到openid，回退到Mock模式");
            return mockLogin(code);
        }

        return doLogin(openid, sessionKey);
    }

    /**
     * Mock登录（开发/演示环境使用）
     */
    private Map<String, Object> mockLogin(String code) {
        String mockOpenid = "mock_openid_" + cn.hutool.crypto.SecureUtil.md5(code).substring(0, 16);
        log.info("Mock登录, openid: {}", mockOpenid);
        return doLogin(mockOpenid, "mock_session_key");
    }

    /**
     * 统一登录流程
     */
    private Map<String, Object> doLogin(String openid, String sessionKey) {
        // 2. 查找或创建用户
        User user = findOrCreateUser(openid);

        // 3. 生成 JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 4. 存储 session 到 Redis
        String redisKey = "token:session:" + user.getId();
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("role", user.getRole() != null ? user.getRole().toUpperCase() : "USER");
        sessionData.put("username", user.getUsername());
        stringRedisTemplate.opsForHash().putAll(redisKey, sessionData);
        stringRedisTemplate.expire(redisKey, 24, TimeUnit.HOURS);

        // 5. 缓存 session_key
        String wxSessionKey = "wx:session:" + openid;
        stringRedisTemplate.opsForValue().set(wxSessionKey, sessionKey, 24, TimeUnit.HOURS);

        // 6. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 7. 构建返回数据
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("token", token);
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole() != null ? user.getRole().toUpperCase() : "USER");
        result.put("openid", openid);

        return result;
    }

    /**
     * 根据 openid 查找用户，不存在则自动创建
     */
    private User findOrCreateUser(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            // 新用户 - 自动注册
            user = new User();
            user.setOpenid(openid);
            user.setUsername("wx_" + openid.substring(0, Math.min(8, openid.length())));
            user.setNickname("微信用户");
            user.setRole("user");
            user.setStatus(1);
            userMapper.insert(user);
            log.info("新用户注册成功, openid: {}, userId: {}", openid, user.getId());
        } else {
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new RuntimeException("账号已被禁用");
            }
            log.info("用户登录成功, openid: {}, userId: {}", openid, user.getId());
        }

        return user;
    }
}
