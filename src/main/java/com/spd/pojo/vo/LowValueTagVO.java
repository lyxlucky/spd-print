package com.spd.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LowValueTagVO {

    @JsonProperty("Def_No_Pkg_Code")
    private String defNoPkgCode;
    @JsonProperty("Manufacturing_Ent_Name")
    private String manufacturingEntName;
    @JsonProperty("Charging_Code")
    private String chargingCode;
    @JsonProperty("Varietie_Name")
    private String varietieName;
    @JsonProperty("Specification_Or_Type")
    private String specificationOrType;
    @JsonProperty("SPECIFICATION_OR_TYPE2")
    private String specificationOrType2;
    @JsonProperty("SPECIFICATION_OR_TYPE3")
    private String specificationOrType3;
    @JsonProperty("Unit")
    private String unit;
    @JsonProperty("Varietie_Code")
    private String varietieCode;
    @JsonProperty("Batch")
    private String batch;
    @JsonProperty("Coefficient")
    private String coefficient;
    @JsonProperty("BATCH_VALIDITY_PERIOD")
    private String batchValidityPeriod;
    @JsonProperty("SUPPLIER_NAME")
    private String supplierName;
    @JsonProperty("SUPPLY_PRICE")
    private int SUPPLY_PRICE;
    @JsonProperty("APPROVAL_NUMBER")
    private String approvalNumber;

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getSpecificationOrType2() {
        return specificationOrType2;
    }

    public void setSpecificationOrType2(String specificationOrType2) {
        this.specificationOrType2 = specificationOrType2;
    }

    public String getSpecificationOrType3() {
        return specificationOrType3;
    }

    public void setSpecificationOrType3(String specificationOrType3) {
        this.specificationOrType3 = specificationOrType3;
    }

    public int getSUPPLY_PRICE() {
        return SUPPLY_PRICE;
    }

    public void setSUPPLY_PRICE(int SUPPLY_PRICE) {
        this.SUPPLY_PRICE = SUPPLY_PRICE;
    }

    public String getDefNoPkgCode() {
        return defNoPkgCode;
    }

    public void setDefNoPkgCode(String defNoPkgCode) {
        this.defNoPkgCode = defNoPkgCode;
    }

    public String getManufacturingEntName() {
        return manufacturingEntName;
    }

    public void setManufacturingEntName(String manufacturingEntName) {
        this.manufacturingEntName = manufacturingEntName;
    }

    public String getChargingCode() {
        return chargingCode;
    }

    public void setChargingCode(String chargingCode) {
        this.chargingCode = chargingCode;
    }

    public String getVarietieName() {
        return varietieName;
    }

    public void setVarietieName(String varietieName) {
        this.varietieName = varietieName;
    }

    public String getSpecificationOrType() {
        return specificationOrType;
    }

    public void setSpecificationOrType(String specificationOrType) {
        this.specificationOrType = specificationOrType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVarietieCode() {
        return varietieCode;
    }

    public void setVarietieCode(String varietieCode) {
        this.varietieCode = varietieCode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public String getBatchValidityPeriod() {
        return batchValidityPeriod;
    }

    public void setBatchValidityPeriod(String batchValidityPeriod) {
        this.batchValidityPeriod = batchValidityPeriod;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "LowValueTagVO{" +
                "defNoPkgCode='" + defNoPkgCode + '\'' +
                ", manufacturingEntName='" + manufacturingEntName + '\'' +
                ", chargingCode='" + chargingCode + '\'' +
                ", varietieName='" + varietieName + '\'' +
                ", specificationOrType='" + specificationOrType + '\'' +
                ", unit='" + unit + '\'' +
                ", varietieCode='" + varietieCode + '\'' +
                ", batch='" + batch + '\'' +
                ", coefficient='" + coefficient + '\'' +
                ", batchValidityPeriod='" + batchValidityPeriod + '\'' +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}
