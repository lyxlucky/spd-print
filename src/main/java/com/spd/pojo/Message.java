package com.spd.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Message {

    @JsonProperty("content")
    private String Content;

    @JsonProperty("summary")
    private String Summary;

    @JsonProperty("contentType")
    private int ContentType;

    @JsonProperty("spt")
    private String Spt;

    @JsonProperty("sptList")
    private String[] SptList;

    @JsonProperty("url")
    private String Url;

    public Message() {
    }

    public Message(String content, String summary, int contentType, String spt, String[] sptList, String url) {
        Content = content;
        Summary = summary;
        ContentType = contentType;
        Spt = spt;
        SptList = sptList;
        Url = url;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public int getContentType() {
        return ContentType;
    }

    public void setContentType(int contentType) {
        ContentType = contentType;
    }

    public String getSpt() {
        return Spt;
    }

    public void setSpt(String spt) {
        Spt = spt;
    }

    public String[] getSptList() {
        return SptList;
    }

    public void setSptList(String[] sptList) {
        SptList = sptList;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    @Override
    public String toString() {
        return "Message{" +
                "Content='" + Content + '\'' +
                ", Summary='" + Summary + '\'' +
                ", ContentType=" + ContentType +
                ", Spt='" + Spt + '\'' +
                ", SptList=" + Arrays.toString(SptList) +
                ", Url='" + Url + '\'' +
                '}';
    }
}
