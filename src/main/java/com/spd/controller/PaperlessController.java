package com.spd.controller;

import com.spd.pojo.dto.DocumentInfo;
import com.spd.pojo.dto.PekingPaperlessDTO;
import com.spd.pojo.dto.SaveDocumentRequestDTO;
import com.spd.pojo.vo.DocumentStatus;
import com.spd.pojo.vo.PdfGenerationResult;
import com.spd.pojo.vo.ResponseVO;
import com.spd.services.PaperlessService;
import com.spd.services.ThirdPartyApiService;
import com.spd.utils.PdfboxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 无纸化文档控制器
 */
@RestController
@RequestMapping("/paperless")
@Slf4j
public class PaperlessController extends BaseController {
    
    @Autowired
    private PaperlessService paperlessService;
    @Autowired
    private PdfboxUtil pdfboxUtil;
    @Autowired
    private ThirdPartyApiService thirdPartyApiService;

    /**
     * 获取无纸化系统访问token
     * 
     * @return token信息
     */
    @GetMapping("/token")
    public ResponseVO getToken() {
        try {
            return success(paperlessService.getToken());
        } catch (IOException e) {
            return error("获取token失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询病案信息
     * 
     * @param uniqueIdentifier 唯一标识符
     * @return 病案信息
     */
    @GetMapping("/query")
    public ResponseVO query(@RequestParam String uniqueIdentifier) {
        try {
            if (uniqueIdentifier == null || uniqueIdentifier.isEmpty()) {
                return error("uniqueIdentifier不能为空");
            }
            return success(paperlessService.query(uniqueIdentifier));
        } catch (IOException e) {
            return error("查询病案信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/generatePdf")
    public ResponseVO generatePdf(@RequestBody PekingPaperlessDTO data) {
        try {
            // 生成PDF并获取结果
            List<PdfGenerationResult> results = paperlessService.generatePdf(data.getBody(), data.getType());
            
            if (results == null || results.isEmpty()) {
                return error("PDF生成失败：没有生成任何文件");
            }
            
            // 检查是否有生成失败的文件
            List<PdfGenerationResult> failedResults = results.stream()
                .filter(result -> !result.isSuccess())
                .collect(java.util.stream.Collectors.toList());
            
            if (!failedResults.isEmpty()) {
                return error("部分PDF生成失败：" + failedResults.get(0).getErrorMessage());
            }
            
            // 所有文件生成成功，返回文件信息
            log.info("PDF生成成功，共生成 {} 个文件", results.size());
            
            // 调用第三方接口，传递文件路径、文档名称和docId
            Map<String, DocumentInfo> documentInfoMap = new HashMap<>();
            for (PdfGenerationResult result : results) {
                DocumentInfo documentInfo = new DocumentInfo();
                documentInfo.setDocumentName(result.getDocumentName());
                documentInfo.setFileRelativePath(result.getFileRelativePath());
                documentInfo.setDocId(result.getDocId());
                documentInfoMap.put(result.getDocumentName(), documentInfo);
            }
            
            // 批量调用第三方接口（包含docId）
            int successCount = thirdPartyApiService.batchUploadDocumentInfo(documentInfoMap);
            log.info("第三方接口调用完成，成功: {}, 总数: {}", successCount, results.size());
            
            return success(results);
            
        } catch (IOException e) {
            return error("生成PDF失败: " + e.getMessage());
        }
    }
    


    
    /**
     * 保存文档到无纸化系统
     * 
     * @param request 保存文档请求参数
     * @return 响应结果
     */
    @PostMapping(value = "/document", consumes = "multipart/form-data")
    public ResponseVO saveDocument(@ModelAttribute SaveDocumentRequestDTO request) {
        try {
            // 参数校验
            if (request.getDocumentName() == null || request.getDocumentName().isEmpty()) {
                return error("文档名称不能为空");
            }
            
            if (request.getDocumentFile() == null || request.getDocumentFile().isEmpty()) {
                return error("文档文件不能为空");
            }
            
            // 验证文件类型，只允许PDF文件
            String originalFilename = request.getDocumentFile().getOriginalFilename();
            String contentType = request.getDocumentFile().getContentType();
            
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                return error("只能上传PDF文件");
            }
            
            if (contentType == null || !contentType.equals("application/pdf")) {
                return error("文件类型必须为PDF格式");
            }
            
            if (request.getStatus() == null) {
                return error("文档状态不能为空");
            }
            
            // 根据传入的状态码获取对应的枚举值
            DocumentStatus documentStatus;
            switch (request.getStatus()) {
                case 0:
                    documentStatus = DocumentStatus.CREATE;
                    break;
                case 1:
                    documentStatus = DocumentStatus.UPDATE;
                    break;
                case 9:
                    documentStatus = DocumentStatus.DELETE;
                    break;
                default:
                    return error("无效的状态码，有效值为: 0(新增), 1(更新), 9(删除)");
            }
            
            // 创建临时PDF文件
            File tempFile = File.createTempFile(request.getDocumentName(), ".pdf");
            request.getDocumentFile().transferTo(tempFile);
            
            // 调用服务保存文档
            return success(paperlessService.saveDocument(
                request.getDocumentName(), 
                tempFile,
                documentStatus));
        } catch (IOException e) {
            return error("保存文档失败: " + e.getMessage());
        }
    }
}