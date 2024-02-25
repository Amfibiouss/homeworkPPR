package com.example.laba;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public_static/**")
                .addResourceLocations("classpath:/static/public_static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .setUseLastModified(true);
            //    .resourceChain(true)
            //    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
}