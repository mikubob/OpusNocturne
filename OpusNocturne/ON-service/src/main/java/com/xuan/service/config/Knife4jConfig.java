package com.xuan.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * knife4j配置类 - OpenAPI3.0
 * @author 玄〤
 * @since 2026-02-17
 */
@Configuration
public class Knife4jConfig {
    /**
     * OpenAPI基本信息配置
     */
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("OpusNocturne博客系统API文档")
                        .description("基于Spring Boot 3.4.2 和 JDK21的现代化博客系统")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("玄〤")
                                .email("2386782347@qq.com")
                                .url("https://github.com/mikubob/OpusNocturne"))
                );
    }

    /**
     * 全部API分组
     */
    @Bean
    public GroupedOpenApi allApi(){
        return GroupedOpenApi.builder()
                .group("0-全部接口")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 系统权限模块API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("1-系统权限模块")
                .pathsToMatch("/api/admin/auth/**", "/api/admin/user/**",
                        "/api/admin/role/**", "/api/admin/permission/**")
                .build();
    }

    /**
     * 博客核心模块API分组
     */
    @Bean
    public GroupedOpenApi blogApi() {
        return GroupedOpenApi.builder()
                .group("2-博客核心模块")
                .pathsToMatch("/api/admin/article/**", "/api/admin/category/**",
                        "/api/admin/tag/**", "/api/blog/**")
                .build();
    }

    /**
     * 互动资源模块API分组
     */
    @Bean
    public GroupedOpenApi interactionApi() {
        return GroupedOpenApi.builder()
                .group("3-互动资源模块")
                .pathsToMatch("/api/admin/comment/**", "/api/admin/attachment/**",
                        "/api/blog/comment/**", "/api/blog/attachment/**")
                .build();
    }

}
