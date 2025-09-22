package com.spd.pojo.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PDF生成结果VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfGenerationResult {
    
    /**
     * 文档名称
     */
    private String documentName;
    
    /**
     * 文件相对路径
     */
    private String fileRelativePath;
    
    /**
     * 文件绝对路径
     */
    private String fileAbsolutePath;
    
    /**
     * 生成状态
     */
    private boolean success;
    
    /**
     * 错误信息（如果生成失败）
     */
    private String errorMessage;

    /// 文档ID
    private String docId;
}
