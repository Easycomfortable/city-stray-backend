package com.citystray.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文工具类 - 基于 ThreadLocal 存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("username", username);
        map.put("role", role);
        CONTEXT.set(map);
    }

    public static Long getUserId() {
        Map<String, Object> map = CONTEXT.get();
        return map == null ? null : (Long) map.get("userId");
    }

    public static String getUsername() {
        Map<String, Object> map = CONTEXT.get();
        return map == null ? null : (String) map.get("username");
    }

    public static String getRole() {
        Map<String, Object> map = CONTEXT.get();
        return map == null ? null : (String) map.get("role");
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
