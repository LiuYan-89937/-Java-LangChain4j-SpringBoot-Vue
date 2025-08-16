package com.liuyan.personalblog.Config;

import com.liuyan.personalblog.Intercepter.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns("/user/login").excludePathPatterns("/user/upload")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/logOut")
                .excludePathPatterns("/follow/getFollowingList");
    }

}
