package com.citystray.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Knife4j 接口文档配置类
 * <p>
 * 配置 Swagger2 接口文档的生成规则，
 * 启动后访问 /doc.html 即可查阅在线接口文档。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Configuration
public class Knife4jConfig {

    /**
     * 创建 RESTful API 文档配置
     *
     * @return Docket 文档构建器实例
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描控制器包路径
                .apis(RequestHandlerSelectors.basePackage("com.citystray.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 API 基本信息
     *
     * @return ApiInfo 接口文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("城流浪 - 城市流浪动物救助管理平台 API")
                .description("后端核心业务接口文档")
                .contact(new Contact("CityStray Team", "", ""))
                .version("1.0.0")
                .build();
    }
}
