package com.spd.utils;

import org.springframework.stereotype.Component;

/**
 * Token上下文工具类
 * 用于在线程本地存储和获取Token信息
 */
@Component
public class TokenContext {
    
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    
    /**
     * 设置当前线程的Token
     * @param token 访问令牌
     */
    public void setToken(String token) {
        tokenHolder.set(token);
    }
    
    /**
     * 获取当前线程的Token
     * @return 访问令牌
     */
    public String getToken() {
        return tokenHolder.get();
    }
    
    /**
     * 清除当前线程的Token
     */
    public void clear() {
        tokenHolder.remove();
    }
}