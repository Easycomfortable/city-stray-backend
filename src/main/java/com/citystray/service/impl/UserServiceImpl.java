package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.entity.User;
import com.citystray.mapper.UserMapper;
import com.citystray.service.UserService;
import com.citystray.util.JwtUtil;
import com.citystray.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> login(String username, String password) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成 JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 存储到 Redis，设置 24 小时过期
        String redisKey = "token:session:" + user.getId();
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("role", user.getRole() != null ? user.getRole().toUpperCase() : "USER");
        sessionData.put("username", user.getUsername());
        stringRedisTemplate.opsForHash().putAll(redisKey, sessionData);
        stringRedisTemplate.expire(redisKey, 24, TimeUnit.HOURS);

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 构建返回数据
        return buildUserInfoMap(user, token);
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return buildUserInfoMap(user, null);
    }

    @Override
    public void logout() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            String redisKey = "token:session:" + userId;
            stringRedisTemplate.delete(redisKey);
        }
    }

    /**
     * 构建用户信息返回 Map
     */
    private Map<String, Object> buildUserInfoMap(User user, String token) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", user.getId());

        if (token != null) {
            result.put("token", token);
        }

        result.put("name", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        result.put("role", role);

        // roles 列表
        List<String> roles = new ArrayList<>();
        roles.add(role);
        result.put("roles", roles);

        // permissions 列表：admin 拥有全部权限
        List<String> permissions = new ArrayList<>();
        if ("ADMIN".equals(role)) {
            permissions.add("*:*:*");
        }
        result.put("permissions", permissions);

        return result;
    }
}
