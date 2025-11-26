package com.spd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Component
public class FileConfig implements WebMvcConfigurer {

    @Autowired
    private PrintConfig printConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/lowValueTagPdfs/**")
                .addResourceLocations("file:" + printConfig.getFileLocation() + "/" + printConfig.getLowValueTagDir());
    }
}
