package com.example.webcvtemplate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthenticationInterceptor authenticationInterceptor;
    private final RoleBasedAuthInterceptor roleBasedAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/reviews/**")
                .excludePathPatterns("/chat");

        // Interceptor kiểm tra quyền truy cập admin
        registry.addInterceptor(roleBasedAuthInterceptor)
                .addPathPatterns("/admin", "/admin/**", "/api/admin/**")
                .excludePathPatterns("/admin-assets/**", "/web/js/**", "/web/css/**", "/web/image/**", "/api/cv/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image_uploads/**")
                .addResourceLocations("file:image_uploads/");

        registry.addResourceHandler("/chat/**")
                .addResourceLocations("classpath:/static/chat/");
         // Đường dẫn tới thư mục thực tế
        registry.addResourceHandler("/admin-assets/**")
                .addResourceLocations("classpath:/static/admin-assets/");

    }






}
