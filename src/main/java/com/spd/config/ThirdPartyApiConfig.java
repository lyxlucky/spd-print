package com.spd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方接口配置
 */
@Component
@ConfigurationProperties(prefix = "paperless.spd")
public class ThirdPartyApiConfig {
    
    /**
     * 第三方接口基础URL
     */
    private String baseUrl;
    
    /**
     * 文档上传接口路径
     */
    private String documentUploadPath;
    
    /**
     * 超时时间（毫秒）
     */
    private int timeout = 30000;
    
    /**
     * 重试次数
     */
    private int retryCount = 3;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDocumentUploadPath() {
        return documentUploadPath;
    }

    public void setDocumentUploadPath(String documentUploadPath) {
        this.documentUploadPath = documentUploadPath;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    /**
     * 获取完整的文档上传URL
     */
    public String getDocumentUploadUrl() {
        return baseUrl + documentUploadPath;
    }
}
