package com.heima.search.config;

import com.heima.search.interceptor.EsTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 19:35
 * @Version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EsTokenInterceptor()).addPathPatterns("/**");
    }
}
