package com.spd.services;

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
     * @param filePathMap 文件路径映射（文档名称 -> 文件相对路径）
     * @return 成功上传的数量
     */
    int batchUploadDocumentInfo(java.util.Map<String, String> filePathMap);
}
