package com.citystray.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置类
 * <p>
 * 允许前端跨域访问后端接口，开发阶段放行所有来源。
 * 生产环境建议限制 allowedOrigin 为具体域名。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 注册跨域过滤器
     *
     * @return CorsFilter 跨域过滤器实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许携带 Cookie
        config.setAllowCredentials(true);
        // 允许所有来源域名（生产环境需替换为具体域名）
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口路径生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
