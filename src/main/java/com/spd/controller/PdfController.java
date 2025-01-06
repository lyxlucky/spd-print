package com.spd.controller;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.config.PrintConfig;
import com.spd.pojo.dto.TagDTO;
import com.spd.pojo.vo.LowValueTagResponseVO;
import com.spd.pojo.vo.ResponseVO;
import com.spd.utils.PdfboxUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/print")
public class PdfController extends BaseController{

    @Autowired
    private PdfboxUtil pdfboxUtil;

    @Autowired
    private PrintConfig printConfig;

    @GetMapping("/lowValueTagPdf")
    public ResponseVO lowValueTag(TagDTO tagDTO){
        String response = HttpUtil.get("http://"+ printConfig.getBaseUrl() +"/api/PrintPdf/GetBDJYKBQJSON?id="+tagDTO.getId()+"&format="+tagDTO.getFormat()+"&inline="+tagDTO.getInline()+"&jsonid="+tagDTO.getJsonid()+"&jsonno="+tagDTO.getJsonno()+"");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LowValueTagResponseVO tag = objectMapper.readValue(response, LowValueTagResponseVO.class);
            if(tag.getCode() == HttpStatus.HTTP_OK){
                if(!tag.getData().isEmpty()){
                    String filename = pdfboxUtil.generateLowValueTag(tag.getData());
                    return success(printConfig.getLowValueTagDir() + "/" + filename);
                }
                return error("无数据可打印");
            }
        } catch (IOException e) {
            return error(e);
        }
        return error("处理失败");
    }
}
