package com.spd.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PaperlessWithGtDTO {
    /**
     * key 类似 "4568022-1"
     */
    @JsonProperty("body")
    private Map<String, OperationRecord> body;

    @JsonProperty("type")
    private String type;

    @Data
    public static class OperationRecord {

        @JsonProperty("SterilizationPackages")
        private List<SterilizationPackage> sterilizationPackages;

        @JsonProperty("ItemsWithoutSterilization")
        private List<MedicalItem> itemsWithoutSterilization;
    }

    @Data
    public static class SterilizationPackage {

        @JsonProperty("DeliveryNoteNumber")
        private String deliveryNoteNumber;

        @JsonProperty("PackageNo")
        private String packageNo;

        @JsonProperty("PackageName")
        private String packageName;

        @JsonProperty("SteriliseTime")
        private String steriliseTime;

        @JsonProperty("ExpireTime")
        private String expireTime;

        @JsonProperty("PackageMaterial")
        private String packageMaterial;

        @JsonProperty("PackageUser")
        private String packageUser;

        @JsonProperty("ConfirmUser")
        private String confirmUser;

        @JsonProperty("SterUser")
        private String sterUser;

        @JsonProperty("SterNo")
        private String sterNo;

        @JsonProperty("SterCycle")
        private String sterCycle;

        @JsonProperty("Items")
        private List<MedicalItem> items;
    }

    @Data
    public static class MedicalItem {

        @JsonProperty("AdnissTimes")
        private String adnissTimes;

        @JsonProperty("DefNoPkgCode")
        private String defNoPkgCode;

        @JsonProperty("ChargingCode")
        private String chargingCode;

        @JsonProperty("VarietieCodeNew")
        private String varietieCodeNew;

        @JsonProperty("VarietieName")
        private String varietieName;

        @JsonProperty("BatchProductionDate")
        private String batchProductionDate;

        @JsonProperty("BatchValidityPeriod")
        private String batchValidityPeriod;

        @JsonProperty("ManufacturingEntName")
        private String manufacturingEntName;

        @JsonProperty("ApprovalNumber")
        private String approvalNumber;

        @JsonProperty("SpecificationOrType")
        private String specificationOrType;

        @JsonProperty("Batch")
        private String batch;

        @JsonProperty("HospitalizationNumber")
        private String hospitalizationNumber;

        @JsonProperty("PatientName")
        private String patientName;

        @JsonProperty("StorageId")
        private String storageId; // JSON 中有 null，这里用 String 接
    }
}
