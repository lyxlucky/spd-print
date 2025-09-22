package com.spd.services;

import com.spd.pojo.dto.DocumentInfo;

/**
 * 第三方接口服务
 */
public interface ThirdPartyApiService {
    
    /**
     * 上传文档信息到第三方系统
     * 
     * @param fileRelativePath 文件相对路径
     * @param documentName 文档名称
     * @return 是否成功
     */
    boolean uploadDocumentInfo(String fileRelativePath, String documentName);
    
    /**
     * 批量上传文档信息
     * 
     * @param documentInfoMap 文档信息映射（文档名称 -> 文档信息对象）
     * @return 成功上传的数量
     */
    int batchUploadDocumentInfo(java.util.Map<String, DocumentInfo> documentInfoMap);
}
