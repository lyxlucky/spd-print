package com.spd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties(prefix = "notify.wx-pusher")
public class WxPusherConfig {

    private String wxPusherBaseurl;

    private String wxPusherSpt;

    public String getWxPusherBaseurl() {
        return wxPusherBaseurl;
    }

    public void setWxPusherBaseurl(String wxPusherBaseurl) {
        this.wxPusherBaseurl = wxPusherBaseurl;
    }

    public String getWxPusherSpt() {
        return wxPusherSpt;
    }

    public void setWxPusherSpt(String wxPusherSpt) {
        this.wxPusherSpt = wxPusherSpt;
    }

    @Override
    public String toString() {
        return "WxPusherConfig{" +
                "wxPusherBaseurl='" + wxPusherBaseurl + '\'' +
                ", wxPusherSpt='" + wxPusherSpt + '\'' +
                '}';
    }
}
