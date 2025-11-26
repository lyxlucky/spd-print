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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 获取当前type配置状态
     */
    @GetMapping("/type/status")
    public ResponseVO getTypeStatus() {
        String currentType = printConfig.getType();
        return success("当前type配置: " + currentType);
    }

    /**
     * 动态更改type配置
     * @param type 新的type值，支持: true, false, random
     */
    @PostMapping("/type/change")
    public ResponseVO changeType(@RequestParam String type) {
        // 参数验证
        if (type == null || type.trim().isEmpty()) {
            return error("type参数不能为空");
        }
        
        type = type.trim().toLowerCase();
        if (!"true".equals(type) && !"false".equals(type) && !"random".equals(type)) {
            return error("type参数值无效，只支持: true, false, random");
        }
        
        // 获取当前配置
        String oldType = printConfig.getType();
        
        // 更新配置
        printConfig.setType(type);
        
        return success("type配置已从 '" + oldType + "' 更改为 '" + type + "'");
    }

    /**
     * 生成测试PDF（包含原始码和包码）
     * 总共4个条码，每页2个标签（每个标签包含一个原始码和一个包码），生成2页
     */
    @GetMapping("/testPackageCodePdf")
    public ResponseVO generateTestPackageCodePdf() {
        try {
            java.io.File pdfFile = pdfboxUtil.generateTestPDFWithPackageCodes();
            return success("PDF生成成功，文件路径: " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            return error("生成PDF失败: " + e.getMessage());
        }
    }

}