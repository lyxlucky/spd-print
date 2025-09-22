package com.spd.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.config.ThirdPartyApiConfig;
import com.spd.pojo.dto.DocumentInfo;
import com.spd.services.ThirdPartyApiService;
import com.spd.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 第三方接口服务实现类
 */
@Service
@Slf4j
public class ThirdPartyApiServiceImpl implements ThirdPartyApiService {

    @Autowired
    private ThirdPartyApiConfig thirdPartyApiConfig;
    
    @Autowired
    private OkHttpUtil okHttpUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 创建线程池用于异步调用
    private final Executor executor = Executors.newFixedThreadPool(5);

    @Override
    public boolean uploadDocumentInfo(String fileRelativePath, String documentName) {
        try {
            log.info("开始上传文档信息到第三方系统: {}, 文件路径: {}", documentName, fileRelativePath);
            
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("documentName", documentName);
            requestBody.put("filePath", fileRelativePath);
            requestBody.put("docId", fileRelativePath);
            requestBody.put("uploadTime", System.currentTimeMillis());
            requestBody.put("sourceSystem", "SPD");
            
            // 构建请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            // 发送请求
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            String response = okHttpUtil.postJson(
                thirdPartyApiConfig.getDocumentUploadUrl(), 
                jsonBody, 
                headers
            );
            
            log.info("第三方接口响应: {}", response);
            
            // 这里可以根据实际响应格式解析结果
            // 示例：假设响应包含 "success": true 表示成功
            if (response != null && response.contains("200")) {
                log.info("文档信息上传成功: {}", documentName);
                return true;
            } else {
                log.warn("文档信息上传失败: {}, 响应: {}", documentName, response);
                return false;
            }
            
        } catch (Exception e) {
            log.error("上传文档信息异常: {}, 错误: {}", documentName, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int batchUploadDocumentInfo(Map<String, DocumentInfo> documentInfoMap) {
        if (documentInfoMap == null || documentInfoMap.isEmpty()) {
            log.warn("文档信息映射为空，跳过批量上传");
            return 0;
        }
        
        log.info("开始批量上传文档信息，共 {} 个文档", documentInfoMap.size());
        
        // 使用CompletableFuture进行异步批量上传
        @SuppressWarnings("unchecked")
        CompletableFuture<Boolean>[] futures = documentInfoMap.entrySet().stream()
            .map(entry -> CompletableFuture.supplyAsync(() -> 
                uploadDocumentInfoWithDocId(entry.getValue()), executor))
            .toArray(CompletableFuture[]::new);
        
        // 等待所有上传完成
        CompletableFuture.allOf(futures).join();
        
        // 统计成功数量
        int successCount = 0;
        for (CompletableFuture<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("获取上传结果异常: {}", e.getMessage());
            }
        }
        
        log.info("批量上传完成，成功: {}, 失败: {}", successCount, documentInfoMap.size() - successCount);
        return successCount;
    }
    
    /**
     * 上传文档信息到第三方系统（包含docId）
     * 
     * @param documentInfo 文档信息
     * @return 是否成功
     */
    private boolean uploadDocumentInfoWithDocId(DocumentInfo documentInfo) {
        try {
            log.info("开始上传文档信息到第三方系统: {}, 文件路径: {}, docId: {}", 
                documentInfo.getDocumentName(), documentInfo.getFileRelativePath(), documentInfo.getDocId());
            
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("documentName", documentInfo.getDocumentName());
            requestBody.put("filePath", documentInfo.getFileRelativePath());
            requestBody.put("docId", documentInfo.getDocId());
            requestBody.put("uploadTime", System.currentTimeMillis());
            requestBody.put("sourceSystem", "SPD");
            
            // 构建请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            // 发送请求
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            String response = okHttpUtil.postJson(
                thirdPartyApiConfig.getDocumentUploadUrl(), 
                jsonBody, 
                headers
            );
            
            log.info("第三方接口响应: {}", response);
            
            // 这里可以根据实际响应格式解析结果
            // 示例：假设响应包含 "success": true 表示成功
            if (response != null && response.contains("200")) {
                log.info("文档信息上传成功: {}, docId: {}", documentInfo.getDocumentName(), documentInfo.getDocId());
                return true;
            } else {
                log.warn("文档信息上传失败: {}, docId: {}, 响应: {}", 
                    documentInfo.getDocumentName(), documentInfo.getDocId(), response);
                return false;
            }
            
        } catch (Exception e) {
            log.error("上传文档信息异常: {}, docId: {}, 错误: {}", 
                documentInfo.getDocumentName(), documentInfo.getDocId(), e.getMessage(), e);
            return false;
        }
    }
}
