package com.citystray.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置分页插件等MyBatis-Plus扩展功能。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器
     * <p>
     * 添加分页插件，支持 MySQL 数据库的物理分页查询。
     * </p>
     *
     * @return MybatisPlusInterceptor 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，指定数据库类型为MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
