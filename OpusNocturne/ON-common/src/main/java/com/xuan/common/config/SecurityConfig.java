package com.xuan.common.config;

import com.xuan.common.handle.AccessDeniedHandlerImpl;
import com.xuan.common.handle.AuthenticationEntryPointImpl;
import com.xuan.common.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity//启用Spring Security
@EnableMethodSecurity//开启注解控制权限（如@PreAuthorize：允许指定用户执行指定操作）
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    //未登录或者token失效时的处理器（401）
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    //已经登录但是访问未授权资源时的处理器（403）
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    /**
     * 创建安全过滤链
     * @param http Http安全配置
     * @return 安全过滤链
     * @throws Exception 抛出异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
         * 禁用CSRF（防止跨站请求伪造）
         * 因为认证凭证在请求头中，不会被浏览器缓存，也不会被浏览器发送给服务器，不需要使用Cookie保存认证凭证
         * 这样攻击者就无法获取认证凭证Authorization头，从而无法进行攻击。
         * 启用CSRF反而会增加开发复杂度，违背了API的设计原则
         * 且本系统采用JWT进行认证，天然免疫CSRF攻击
         */
        http.csrf(AbstractHttpConfigurer::disable);

        /*
         * 配置会话管理为无状态，这样服务器就不会保存任何会话信息，所有的认证信息都是由客户端发送的认证信息进行验证
         * 这样做符合了REST架构的架构原则，RESTful API应该无状态，不保存任何会话信息
         * 同时避免了不必要的session创建，完全依赖于客户端发送的认证信息请求头Authorization进行验证
         */
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /*
         * 配置异常处理返回逻辑
         * 默认情况下，Spring Security会返回一个默认的错误页面，包含错误信息，但是对于API来说，返回一个错误页面通常不是 desired behavior
         */
        http.exceptionHandling(handler->handler
                        .authenticationEntryPoint(authenticationEntryPoint)//登录失败处理,401
                        .accessDeniedHandler(accessDeniedHandler)//访问未授权资源处理,403
                );

        /*
         * 配置请求路径的放行规则（RBAC初级拦截）
         */
        http.authorizeHttpRequests(auth -> auth
                // 1. 登录与基础认证相关接口：全面放行
                .requestMatchers("/api/admin/auth/login", "/api/admin/auth/register", "/api/admin/auth/captcha")
                .permitAll()

                // 2. Swagger/Knife4j 文档资源：全面放行，方便开发者调试
                .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-resources/**")
                .permitAll()

                // 3. 门户前台展示类接口：放行只读请求（文章列表、详情、分类、评论查询等）
                .requestMatchers("/api/blog/article/**", "/api/blog/category/**", "/api/blog/tag/**",
                        "/api/blog/comment/tree/**")
                .permitAll()

                // 4.其余所有请求（如：管理后台CRUD、前台发表评论等），必须经过认证后才能访问（持有有效的JWT）
                .anyRequest().authenticated()
        );
        
        return http.build();
    }

    /**
     * 创建JWT拦截器
     * @return JWT拦截器
     */
    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        registry.addInterceptor(jwtInterceptor())
                // 配置拦截路径
                .addPathPatterns("/api/**")
                // 配置排除路径
                .excludePathPatterns("/api/auth/**");
    }
}
