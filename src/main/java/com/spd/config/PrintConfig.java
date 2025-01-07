package com.spd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties(prefix = "print.settings")
public class PrintConfig {

    private  String zqBaseUrl;

    private String bdBaseUrl;

    private String lgBaseUrl;

    private String hnBaseUrl;

    private String fyBaseUrl;

    private String smBaseUrl;

    private String fileLocation;

    private String lowValueTagDir;

    public String getLowValueTagDir() {
        return lowValueTagDir;
    }

    public void setLowValueTagDir(String lowValueTagDir) {
        this.lowValueTagDir = lowValueTagDir;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getZqBaseUrl() {
        return zqBaseUrl;
    }

    public void setZqBaseUrl(String zqBaseUrl) {
        this.zqBaseUrl = zqBaseUrl;
    }

    public String getBdBaseUrl() {
        return bdBaseUrl;
    }

    public void setBdBaseUrl(String bdBaseUrl) {
        this.bdBaseUrl = bdBaseUrl;
    }

    public String getLgBaseUrl() {
        return lgBaseUrl;
    }

    public void setLgBaseUrl(String lgBaseUrl) {
        this.lgBaseUrl = lgBaseUrl;
    }

    public String getHnBaseUrl() {
        return hnBaseUrl;
    }

    public void setHnBaseUrl(String hnBaseUrl) {
        this.hnBaseUrl = hnBaseUrl;
    }

    public String getFyBaseUrl() {
        return fyBaseUrl;
    }

    public void setFyBaseUrl(String fyBaseUrl) {
        this.fyBaseUrl = fyBaseUrl;
    }

    public String getSmBaseUrl() {
        return smBaseUrl;
    }

    public void setSmBaseUrl(String smBaseUrl) {
        this.smBaseUrl = smBaseUrl;
    }
}
