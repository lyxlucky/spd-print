package com.spd.utils;

import com.spd.pojo.vo.DocumentStatus;
import com.spd.services.PaperlessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * PDF处理器
 * 用于异步处理PDF生成和上传
 */
@Component
@Slf4j
public class PdfProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 异步处理PDF生成和上传
     * @param documentName 文档名称
     * @param pdfFile PDF文件
     * @param status 文档状态
     * @return CompletableFuture包装的处理结果
     */
    @Async
    public CompletableFuture<Boolean> processPdfAsync(String documentName, File pdfFile, DocumentStatus status) {
        try {
            log.info("开始异步处理PDF: {}, 文件大小: {} bytes", documentName, pdfFile.length());
            
            // 通过ApplicationContext获取PaperlessService实例，避免循环依赖
            PaperlessService paperlessService = applicationContext.getBean(PaperlessService.class);
            
            // 调用saveDocument方法上传PDF
            paperlessService.saveDocument(documentName, pdfFile, status);
            
            log.info("PDF处理完成: {}", documentName);
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("PDF处理失败: {}, 错误信息: {}", documentName, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 批量处理PDF文件
     * @param pdfFiles Map<文档名称, PDF文件>
     * @param status 文档状态
     */
    public void processBatchPdfs(Map<String, File> pdfFiles, DocumentStatus status) {
        log.info("开始批量处理 {} 个PDF文件", pdfFiles.size());
        
        pdfFiles.forEach((documentName, pdfFile) -> {
            processPdfAsync(documentName, pdfFile, status)
                .thenAccept(success -> {
                    if (success) {
                        log.info("PDF {} 处理成功", documentName);
                    } else {
                        log.error("PDF {} 处理失败", documentName);
                    }
                });
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}