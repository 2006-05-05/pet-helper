package com.java1234.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 配置类
 *
 * <p>将 D 盘 uploads 目录映射为静态资源路径，方便前端与小程序访问上传的图片。</p>
 *
 * @author Java1234
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 文件上传根目录 */
    @Value("${pet.upload.path}")
    private String uploadPath;

    /** 文件访问前缀 */
    @Value("${pet.upload.prefix}")
    private String uploadPrefix;

    /**
     * 添加静态资源映射
     *
     * @param registry 资源映射注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + new File(uploadPath).getAbsolutePath() + File.separator;
        registry.addResourceHandler(uploadPrefix + "/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
