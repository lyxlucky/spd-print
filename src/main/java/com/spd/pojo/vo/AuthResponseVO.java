package com.spd.pojo.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * 认证响应实体类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponseVO {
    private Integer code;
    private String msg;
    @JsonDeserialize(using = DataDeserializer.class)
    private Object data;  // 使用自定义反序列化器
    
    private Boolean error;
    private Boolean success;

    /**
     * 内部数据类，用于token相关的响应
     */
    public static class Data {
        @JsonProperty("access_token")
        private String accessToken;
        
        private String logSeqId;
        
        @JsonProperty("expires_in")
        private Integer expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getLogSeqId() {
            return logSeqId;
        }

        public void setLogSeqId(String logSeqId) {
            this.logSeqId = logSeqId;
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
    
    /**
     * 自定义反序列化器，用于处理data字段的不同类型
     */
    public static class DataDeserializer extends JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            
            // 如果是对象节点，尝试反序列化为Data对象
            if (node.isObject()) {
                try {
                    return p.getCodec().treeToValue(node, Data.class);
                } catch (Exception e) {
                    // 如果反序列化为Data对象失败，返回原始节点
                    return p.getCodec().treeToValue(node, Object.class);
                }
            }
            // 如果是其他类型（数字、字符串等），直接返回
            else if (node.isNumber()) {
                return node.asLong();
            } else if (node.isTextual()) {
                return node.asText();
            } else if (node.isBoolean()) {
                return node.asBoolean();
            } else {
                return node.toString();
            }
        }
    }
}