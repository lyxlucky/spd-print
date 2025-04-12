package com.spd.pojo.dto;


public class TagDTO {

    private String id;

    private String format;

    private String inline;

    private String jsonid;

    private String jsonno;

    private String hospitalId;

    public boolean isEnable;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getInline() {
        return inline;
    }

    public void setInline(String inline) {
        this.inline = inline;
    }

    public String getJsonid() {
        return jsonid;
    }

    public void setJsonid(String jsonid) {
        this.jsonid = jsonid;
    }

    public String getJsonno() {
        return jsonno;
    }

    public void setJsonno(String jsonno) {
        this.jsonno = jsonno;
    }
}
