package com.cvsgo.config;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    private final AuthInterceptor authInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/", "/docs/**", "/*.ico", "/api/auth/login", "/api/users",
                "/api/tags", "/api/users/emails/*/exists", "/api/users/nicknames/*/exists",
                "/api/products", "/api/products/*", "/api/products/*/tags", "/api/users/*/reviews",
                "/error");
    }
}
