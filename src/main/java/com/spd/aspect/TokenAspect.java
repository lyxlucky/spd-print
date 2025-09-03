package com.spd.aspect;

import com.spd.annotation.RequiresToken;
import com.spd.pojo.vo.AuthResponseVO;
import com.spd.services.PaperlessService;
import com.spd.utils.TokenContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Token切面类
 * 在调用需要token的方法前自动获取并设置token
 */
@Aspect
@Component
public class TokenAspect {
    
    @Autowired
    private PaperlessService paperlessService;
    
    @Autowired
    private TokenContext tokenContext;
    
    private String accessToken;
    private long tokenExpireTime;
    
    /**
     * 在标记了@RequiresToken注解的方法执行前获取token
     * 
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(requiresToken)")
    public Object handleTokenBeforeSaveDocument(ProceedingJoinPoint joinPoint, RequiresToken requiresToken) throws Throwable {
        try {
            // 检查token是否有效，如果无效则重新获取
            if (isTokenExpired()) {
                AuthResponseVO tokenResponse = paperlessService.getToken();
                if (tokenResponse != null && tokenResponse.getData() != null) {
                    // 处理data字段，可能是Data对象或数字
                    if (tokenResponse.getData() instanceof AuthResponseVO.Data) {
                        AuthResponseVO.Data data = (AuthResponseVO.Data) tokenResponse.getData();
                        accessToken = data.getAccessToken();
                        // 设置过期时间（当前时间 + expires_in（秒）- 提前5秒刷新）
                        tokenExpireTime = System.currentTimeMillis() + (data.getExpiresIn() - 5) * 1000L;
                    } else {
                        throw new IOException("获取token失败：data字段格式不正确，实际类型: " + tokenResponse.getData().getClass().getSimpleName());
                    }
                } else {
                    throw new IOException("获取token失败");
                }
            }
            
            // 将token设置到上下文中
            tokenContext.setToken(accessToken);
            
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 清除上下文中的token
            tokenContext.clear();
        }
    }
    
    /**
     * 检查token是否过期
     * 
     * @return 是否过期
     */
    private boolean isTokenExpired() {
        return accessToken == null || System.currentTimeMillis() >= tokenExpireTime;
    }
    
}