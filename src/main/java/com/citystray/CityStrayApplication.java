package com.citystray;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 城流浪 - 城市流浪动物救助管理平台启动类
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.citystray.mapper")
public class CityStrayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityStrayApplication.class, args);
    }
}
