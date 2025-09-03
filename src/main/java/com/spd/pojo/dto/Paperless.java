package com.spd.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Paperless {
    // 住院号
    @JsonProperty("HospitalizationNumber")
    private String HospitalizationNumber;
    // 定数码
    @JsonProperty("DefNoPkgCode")
    private String DefNoPkgCode;
    //次数
    @JsonProperty("AdnissTimes")
    private String AdnissTimes;
    //计费编码
    @JsonProperty("ChargingCode")
    private String ChargingCode;
    //品种编码
    @JsonProperty("VarietieCodeNew")
    private String VarietieCodeNew;
    //品种名称
    @JsonProperty("VarietieName")
    private String VarietieName;
    //生产日期
    @JsonProperty("BatchProductionDate")
    private String BatchProductionDate;
    //有效期
    @JsonProperty("BatchValidityPeriod")
    private String BatchValidityPeriod;
    //生产厂家
    @JsonProperty("ManufacturingEntName")
    private String ManufacturingEntName;
    //批准文号
    @JsonProperty("ApprovalNumber")
    private String ApprovalNumber;
    //规格型号
    @JsonProperty("SpecificationOrType")
    private String SpecificationOrType;
    //批号
    @JsonProperty("Batch")
    private String Batch;
    // 患者姓名
    @JsonProperty("PatientName")
    private String PatientName;
}