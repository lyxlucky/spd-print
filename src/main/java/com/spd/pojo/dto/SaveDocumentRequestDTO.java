package com.spd.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 保存文档请求DTO
 * 用于封装保存文档到无纸化系统的请求参数
 */
@Data
public class SaveDocumentRequestDTO {
    
    /**
     * 文档名称
     */
    private String documentName;
    
    /**
     * 文档文件
     */
    private MultipartFile documentFile;
    
    /**
     * 文档状态 (0：新增；1：更新；9：删除)
     */
    private Integer status;
}