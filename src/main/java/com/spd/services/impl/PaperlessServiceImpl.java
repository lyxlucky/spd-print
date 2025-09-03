package com.spd.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.annotation.RequiresToken;
import com.spd.config.PaperlessConfig;
import com.spd.pojo.dto.Paperless;
import com.spd.pojo.dto.SkValidRequestDTO;
import com.spd.pojo.vo.AuthResponseVO;
import com.spd.pojo.vo.DocumentStatus;
import com.spd.pojo.vo.PdfGenerationResult;
import com.spd.services.PaperlessService;
import com.spd.utils.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PaperlessServiceImpl implements PaperlessService {

    @Autowired
    private PaperlessConfig paperlessConfig;

    @Autowired
    private OkHttpUtil okHttpUtil;
    
    @Autowired
    private MD5Util MD5Util;
    
    @Autowired
    private TokenContext tokenContext;
    
    @Autowired
    private FileHashUtil fileHashUtil;

    @Autowired
    private PdfboxUtil pdfboxUtil;

    @Autowired
    private PdfProcessor pdfProcessor;

    private final Map<String,String> commonHeaders = new HashMap<>(2);

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        commonHeaders.put("appId", paperlessConfig.getAppId());
        commonHeaders.put("accessKey", paperlessConfig.getAccessKey());
    }

    @Override
    public AuthResponseVO getToken() throws IOException {
        String url = "http://" + paperlessConfig.getDomainUrl() + "/plss/prod-api/open/med/v1/appid/auth";
        String skValid = MD5Util.md5(paperlessConfig.getAccessKey() + paperlessConfig.getSecretKey());
        SkValidRequestDTO request = new SkValidRequestDTO(skValid);
        String json = mapper.writeValueAsString(request);
        String response = okHttpUtil.postJson(url, json,commonHeaders);
        return mapper.readValue(response, AuthResponseVO.class);
    }

    @Override
    @RequiresToken
    public AuthResponseVO query(String uniqueIdentifier) throws IOException {
        String url = "http://" + paperlessConfig.getDomainUrl() + "/plss/prod-api/open/med/medrecord_archiving/v1/medPatientInfo/medicalRecordStatus";
        // 准备请求头
        Map<String, String> headers = new HashMap<>(commonHeaders);
        // 从上下文中获取token并添加到请求头
        String token = tokenContext.getToken();
        headers.put("plss-auth", token);
        Map<String, String> formData = new HashMap<>();
        formData.put("uniqueIdentifier", uniqueIdentifier);
        String response = okHttpUtil.postJson(url, formData, headers);
        log.info("病案查询接口反参{}",response);
        return mapper.readValue(response, AuthResponseVO.class);
    }

    @Override
    public List<PdfGenerationResult> generatePdf(Map<String, List<Paperless>> body,String type) throws IOException {
        try {
            log.info("开始批量生成PDF，共 {} 个文档", body.size());
            
            // 使用新的PDF工具类生成多个PDF文件
            Map<String, File> pdfFiles = pdfboxUtil.generateMultiplePaperlessPDFs(body);
            
            if (pdfFiles.isEmpty()) {
                log.warn("没有生成任何PDF文件");
                return new ArrayList<>();
            }
            
            log.info("PDF生成完成，共生成 {} 个文件", pdfFiles.size());
            
            // 构建返回结果列表
            List<PdfGenerationResult> results = new ArrayList<>();
            
            for (Map.Entry<String, File> entry : pdfFiles.entrySet()) {
                String documentName = entry.getKey();
                File pdfFile = entry.getValue();
                
                // 计算相对路径
                String relativePath = calculateRelativePath(pdfFile);
                
                PdfGenerationResult result = new PdfGenerationResult();
                result.setDocumentName(documentName);
                result.setFileRelativePath(relativePath);
                result.setFileAbsolutePath(pdfFile.getAbsolutePath());
                result.setSuccess(true);
                result.setErrorMessage(null);
                
                results.add(result);
                
                log.info("PDF生成成功: {}, 相对路径: {}, 绝对路径: {}", 
                    documentName, relativePath, pdfFile.getAbsolutePath());
            }
            
            // 异步处理每个PDF文件（上传、记录日志等）
            pdfProcessor.processBatchPdfs(pdfFiles, "0".equals(type) ? DocumentStatus.CREATE : DocumentStatus.UPDATE);
            
            log.info("PDF异步处理已启动，共 {} 个文件", pdfFiles.size());
            
            return results;
            
        } catch (Exception e) {
            log.error("PDF生成失败: {}", e.getMessage(), e);
            PdfGenerationResult errorResult = new PdfGenerationResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage("PDF生成失败: " + e.getMessage());
            
            List<PdfGenerationResult> errorResults = new ArrayList<>();
            errorResults.add(errorResult);
            return errorResults;
        }
    }
    
    /**
     * 计算文件的相对路径
     * @param file 文件对象
     * @return 相对路径
     */
    private String calculateRelativePath(File file) {
        try {
            String absolutePath = file.getAbsolutePath();
            String basePath = paperlessConfig.getFileLocation();
            
            if (absolutePath.startsWith(basePath)) {
                return absolutePath.substring(basePath.length()).replace('\\', '/');
            }
            
            // 如果无法计算相对路径，返回文件名
            return file.getName();
        } catch (Exception e) {
            log.warn("计算相对路径失败，返回文件名: {}", e.getMessage());
            return file.getName();
        }
    }

    @Override
    @RequiresToken
    public AuthResponseVO saveDocument(String documentName, File documentUrl, DocumentStatus status) throws IOException {
        String url = "http://" + paperlessConfig.getDomainUrl() + "/plss/prod-api/open/med/medrecord_archiving/v1/medDocument/save";
        // 准备请求头
        Map<String, String> headers = new HashMap<>(commonHeaders);
        // 从上下文中获取token并添加到请求头
        String token = tokenContext.getToken();
        headers.put("plss-auth", token);
        // 准备表单数据
        Map<String, String> formData = new HashMap<>();
        Map<String, File> formFile = new HashMap<>();

        // 计算文件SHA256哈希值作为docId，确保同一文件ID一致
        String docId = fileHashUtil.calculateSHA256(documentUrl);
        
        formData.put("uniqueIdentifier", documentName);
        formData.put("docName", documentName);
        formData.put("catalogueId", "378476708242757");
        formData.put("docId", docId);
        formData.put("systemTag", "SPD");
        formFile.put("fileData", documentUrl);
        formData.put("status", String.valueOf(status.getCode()));
        formData.put("mode", "10");
        log.info("保存文档, documentName: {}, docId: {}, status: {}", documentName, docId, status);
        // 发送multipart/form-data请求
        String response = okHttpUtil.postMultipartFormData(url, formData, formFile,headers);
        log.info(response);
        return mapper.readValue(response, AuthResponseVO.class);
    }
}