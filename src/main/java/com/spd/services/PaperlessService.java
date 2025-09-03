package com.spd.services;

import com.spd.pojo.dto.Paperless;
import com.spd.pojo.vo.AuthResponseVO;
import com.spd.pojo.vo.DocumentStatus;
import com.spd.pojo.vo.PdfGenerationResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PaperlessService {
    /// 获取Token
    public AuthResponseVO getToken() throws IOException;

    public AuthResponseVO saveDocument(String documentName, File documentUrl, DocumentStatus status) throws IOException;

    public AuthResponseVO query(String uniqueIdentifier) throws IOException;

    public List<PdfGenerationResult> generatePdf(Map<String, List<Paperless>> body,String type) throws IOException;

}