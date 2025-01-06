package com.spd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties(prefix = "print.settings")
public class PrintConfig {

    private  String baseUrl;

    private String fileLocation;

    private String lowValueTagDir;

    public String getLowValueTagDir() {
        return lowValueTagDir;
    }

    public void setLowValueTagDir(String lowValueTagDir) {
        this.lowValueTagDir = lowValueTagDir;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
