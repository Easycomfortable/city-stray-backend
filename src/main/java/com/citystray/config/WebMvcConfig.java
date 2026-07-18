package com.citystray.config;

import com.citystray.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 用户认证
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/logout",
                        // 微信小程序登录
                        "/api/wx/login",
                        // 微信支付回调（微信服务器调用，无JWT）
                        "/api/wx/pay/notify",
                        // 文件上传/下载
                        "/api/upload",
                        "/api/upload/batch",
                        "/uploads/**",
                        // 小程序端公开浏览接口
                        "/api/content/banner/list",
                        "/api/content/story/list",
                        "/api/content/notice/list",
                        "/api/content/article/list",
                        "/api/animal/list",
                        "/api/animal/*",
                        "/api/hospital/list",
                        "/api/finance/project/list",
                        // 接口文档
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/favicon.ico"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传文件静态资源映射（使用绝对路径避免相对路径解析问题）
        String uploadPath = new java.io.File("uploads").getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
