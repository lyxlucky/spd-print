package com.spd.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SkValid请求DTO
 * 用于封装包含sk_valid字段的请求体
 */
@Data
public class SkValidRequestDTO {
    
    /**
     * SK验证值
     */
    @JsonProperty("sk_valid")
    private String skValid;
    
    public SkValidRequestDTO() {
    }
    
    public SkValidRequestDTO(String skValid) {
        this.skValid = skValid;
    }
}