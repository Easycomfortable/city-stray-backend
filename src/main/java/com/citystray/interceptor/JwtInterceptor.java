package com.citystray.interceptor;

import com.citystray.util.JwtUtil;
import com.citystray.util.UserContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或令牌已过期\"}");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);

            // 检查Redis中是否存在活跃会话（支持主动登出）
            String sessionKey = "token:session:" + userId;
            Boolean hasSession = stringRedisTemplate.hasKey(sessionKey);
            if (Boolean.FALSE.equals(hasSession)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"会话已失效，请重新登录\"}");
                return false;
            }

            // 从Redis中获取角色信息
            String role = stringRedisTemplate.opsForHash().get(sessionKey, "role") != null
                    ? stringRedisTemplate.opsForHash().get(sessionKey, "role").toString()
                    : "user";

            // 存入 ThreadLocal
            UserContext.set(userId, username, role);
            return true;
        } catch (Exception e) {
            log.warn("JWT解析失败: {}", e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"令牌无效或已过期\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
