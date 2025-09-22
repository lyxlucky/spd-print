package com.spd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.config.PrintConfig;
import com.spd.pojo.dto.TagDTO;
import com.spd.pojo.vo.LowValueTagResponseVO;
import com.spd.pojo.vo.ResponseVO;
import com.spd.utils.BaseUrlFactory;
import com.spd.utils.PdfboxUtil;
import com.spd.utils.OkHttpUtil;

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
    @Autowired
    private BaseUrlFactory baseUrlFactory;
    @Autowired
    private OkHttpUtil okHttpUtil;

    @GetMapping("/lowValueTagPdf")
    public ResponseVO lowValueTag(TagDTO tagDTO){
        // 检查type配置
        String type = printConfig.getType();
        if("false".equals(type)){
            return error("无数据可打印");
        }
        
        // 构建请求URL
        String url = "http://"+ baseUrlFactory.declareBaseUrl(tagDTO.getHospitalId()) +"/api/PrintPdf/GetBDJYKBQJSON?id="+tagDTO.getId()+"&format="+tagDTO.getFormat()+"&inline="+tagDTO.getInline()+"&jsonid="+tagDTO.getJsonid()+"&jsonno="+tagDTO.getJsonno()+"";

        try {
            // 使用OkHttpUtil工具类发送GET请求
            String responseBody = okHttpUtil.get(url);

            ObjectMapper objectMapper = new ObjectMapper();
            LowValueTagResponseVO tag = objectMapper.readValue(responseBody, LowValueTagResponseVO.class);
            if(tag.getCode() == 200){
                if(!tag.getData().isEmpty()){
                    if("261".equals(tagDTO.getHospitalId())){
                        String filename = pdfboxUtil.generatePekingLowValueTag(tag.getData(), "random".equals(type));
                        return success(printConfig.getLowValueTagDir() + "/" + filename);
                    }else{
                        String filename = pdfboxUtil.generateLowValueTag(tag.getData(), "random".equals(type));
                        return success(printConfig.getLowValueTagDir() + "/" + filename);
                    }
                }
                return error("无数据可打印");
            }
            return error("无数据可打印");
        } catch (IOException e) {
            return error(e);
        }
    }



}