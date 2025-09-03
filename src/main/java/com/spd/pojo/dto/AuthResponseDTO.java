package com.spd.pojo.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 认证响应实体类
 */
@Data
public class AuthResponseDTO {
    private Integer code;
    private String msg;
    private Data data;
    private Boolean error;
    private Boolean success;

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
}