package com.spd.pojo.vo;


import com.alibaba.excel.annotation.ExcelProperty;

public class VarExcelVO {

    @ExcelProperty("启用状态")
    private String enabledStatus;

    @ExcelProperty("品种(材料)编码")
    private String materialCode;

    @ExcelProperty("医保编码27位")
    private String medicalInsuranceCode27;

    @ExcelProperty("计费编码")
    private String chargeCode;

    @ExcelProperty("注册证名称")
    private String registrationCertificateName;

    @ExcelProperty("品种名称")
    private String materialName;

    @ExcelProperty("型号/规格")
    private String modelSpec;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("规格")
    private String specification;

    @ExcelProperty("分类属性2")
    private String classificationAttribute2;

    @ExcelProperty("生产企业名称")
    private String manufacturerName;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("中标价")
    private String bidPrice;

    @ExcelProperty("历史中标价格")
    private String historicalBidPrice;

    @ExcelProperty("批准文号")
    private String approvalNumber;

    @ExcelProperty("原注册证")
    private String originalRegistrationCertificate;

    @ExcelProperty("启用合同供应商")
    private String enabledContractSupplier;

    @ExcelProperty("启用合同")
    private String enabledContract;

    @ExcelProperty("合同类型")
    private String contractType;

    @ExcelProperty("包装规格")
    private String packageSpec;

    @ExcelProperty("中包装")
    private String mediumPackage;

    @ExcelProperty("大包装")
    private String largePackage;

    @ExcelProperty("库存上限")
    private String stockUpperLimit;

    @ExcelProperty("库存下限")
    private String stockLowerLimit;

    @ExcelProperty("省平台编码")
    private String provincialPlatformCode;

    @ExcelProperty("原计费编码")
    private String originalChargeCode;

    @ExcelProperty("阳光产品码")
    private String sunshineProductCode;

    @ExcelProperty("医保编码")
    private String medicalInsuranceCode;

    @ExcelProperty("全国历史销售最低价")
    private String nationalLowestHistoricalSalesPrice;

    @ExcelProperty("上药ERP品种编码")
    private String shangyaoErpMaterialCode;

    @ExcelProperty("高低值分类下级属性")
    private String highLowValueSubClassification;

    @ExcelProperty("主控库区")
    private String mainStorageZone;

    @ExcelProperty("阳光规格型号码")
    private String sunshineSpecModelNumber;

    @ExcelProperty("阳光换算比")
    private String sunshineConversionRate;

    @ExcelProperty("医保一级目录")
    private String insuranceCatalogLevel1;

    @ExcelProperty("医保二级目录")
    private String insuranceCatalogLevel2;

    @ExcelProperty("医保三级目录")
    private String insuranceCatalogLevel3;

    @ExcelProperty("材质")
    private String material;

    @ExcelProperty("产品类型")
    private String productType;

    @ExcelProperty("管理类型")
    private String managementType;

    @ExcelProperty("监管类别")
    private String supervisionCategory;

    @ExcelProperty("监管类别编码")
    private String supervisionCategoryCode;

    @ExcelProperty("是否收费")
    private String isCharged;

    @ExcelProperty("高低值分类")
    private String highLowValueClassification;

    @ExcelProperty("一次性灭菌包装")
    private String disposableSterilePackaging;

    @ExcelProperty("是否一物一码")
    private String isOneItemOneCode;

    @ExcelProperty("存储条件")
    private String storageCondition;

    @ExcelProperty("是否植入")
    private String isImplanted;

    @ExcelProperty("是否介入")
    private String isInterventional;

    @ExcelProperty("是否中标")
    private String isBidWinning;

    @ExcelProperty("是否专购")
    private String isExclusivePurchase;

    @ExcelProperty("使用级别")
    private String usageLevel;

    @ExcelProperty("是否防控物资")
    private String isEpidemicControlMaterial;

    @ExcelProperty("默认货位")
    private String defaultStorageLocation;

    @ExcelProperty("器械识别码（DI)")
    private String deviceIdentifier;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("最近更新时间")
    private String lastUpdateTime;

    @ExcelProperty("财务分类属性")
    private String financialClassification;

    @ExcelProperty("医用类别分类")
    private String medicalCategoryClassification;

    @ExcelProperty("发送阳光平台")
    private String sentToSunshinePlatform;

    @ExcelProperty("发送阳光时间")
    private String sunshineSendTime;

    @ExcelProperty("最近入库时间")
    private String lastInboundTime;

    @ExcelProperty("最近出库时间")
    private String lastOutboundTime;

    @ExcelProperty("高值重点治理序号")
    private String highValueGovernanceSerial;

    @ExcelProperty("重点治理耗材名称")
    private String governedConsumableName;

    @ExcelProperty("阳光平台来源")
    private String sunshinePlatformSource;

    @ExcelProperty("总目录编码")
    private String masterCatalogCode;

    @ExcelProperty("专机专用")
    private String exclusiveMachineUse;

    @ExcelProperty("仓库名称")
    private String warehouseName;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("原编码")
    private String originalCode;

    @ExcelProperty("服务费率%")
    private String serviceFeeRate;

    @ExcelProperty("UDI")
    private String udi;

    @ExcelProperty("供应商备注")
    private String supplierRemark;

    @ExcelProperty("第一批重点耗材")
    private String firstBatchGovernedConsumables;

    @ExcelProperty("是否集采")
    private String isCentralizedProcurement;

    @ExcelProperty("审批时间")
    private String approvalTime;

    @ExcelProperty("审批人")
    private String approver;

    @ExcelProperty("原医院系统编码")
    private String originalHospitalSystemCode;

    @ExcelProperty("合同起始日期")
    private String contractStartDate;

    @ExcelProperty("合同终止日期")
    private String contractEndDate;

    @ExcelProperty("合同明细起始日期")
    private String contractDetailStartDate;

    @ExcelProperty("合同明细终止日期")
    private String contractDetailEndDate;

    @ExcelProperty("HIS单位")
    private String hisUnit;

    @ExcelProperty("HIS价格")
    private String hisPrice;

    @ExcelProperty("HIS转换比")
    private String hisConversionRatio;

    @ExcelProperty("是否挂起")
    private String isSuspended;

    @ExcelProperty("是否贯标")
    private String isStandardized;


    public String getEnabledStatus() {
        return enabledStatus;
    }

    public void setEnabledStatus(String enabledStatus) {
        this.enabledStatus = enabledStatus;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMedicalInsuranceCode27() {
        return medicalInsuranceCode27;
    }

    public void setMedicalInsuranceCode27(String medicalInsuranceCode27) {
        this.medicalInsuranceCode27 = medicalInsuranceCode27;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getRegistrationCertificateName() {
        return registrationCertificateName;
    }

    public void setRegistrationCertificateName(String registrationCertificateName) {
        this.registrationCertificateName = registrationCertificateName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getModelSpec() {
        return modelSpec;
    }

    public void setModelSpec(String modelSpec) {
        this.modelSpec = modelSpec;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getClassificationAttribute2() {
        return classificationAttribute2;
    }

    public void setClassificationAttribute2(String classificationAttribute2) {
        this.classificationAttribute2 = classificationAttribute2;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getHistoricalBidPrice() {
        return historicalBidPrice;
    }

    public void setHistoricalBidPrice(String historicalBidPrice) {
        this.historicalBidPrice = historicalBidPrice;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getOriginalRegistrationCertificate() {
        return originalRegistrationCertificate;
    }

    public void setOriginalRegistrationCertificate(String originalRegistrationCertificate) {
        this.originalRegistrationCertificate = originalRegistrationCertificate;
    }

    public String getEnabledContractSupplier() {
        return enabledContractSupplier;
    }

    public void setEnabledContractSupplier(String enabledContractSupplier) {
        this.enabledContractSupplier = enabledContractSupplier;
    }

    public String getEnabledContract() {
        return enabledContract;
    }

    public void setEnabledContract(String enabledContract) {
        this.enabledContract = enabledContract;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getPackageSpec() {
        return packageSpec;
    }

    public void setPackageSpec(String packageSpec) {
        this.packageSpec = packageSpec;
    }

    public String getMediumPackage() {
        return mediumPackage;
    }

    public void setMediumPackage(String mediumPackage) {
        this.mediumPackage = mediumPackage;
    }

    public String getLargePackage() {
        return largePackage;
    }

    public void setLargePackage(String largePackage) {
        this.largePackage = largePackage;
    }

    public String getStockUpperLimit() {
        return stockUpperLimit;
    }

    public void setStockUpperLimit(String stockUpperLimit) {
        this.stockUpperLimit = stockUpperLimit;
    }

    public String getStockLowerLimit() {
        return stockLowerLimit;
    }

    public void setStockLowerLimit(String stockLowerLimit) {
        this.stockLowerLimit = stockLowerLimit;
    }

    public String getProvincialPlatformCode() {
        return provincialPlatformCode;
    }

    public void setProvincialPlatformCode(String provincialPlatformCode) {
        this.provincialPlatformCode = provincialPlatformCode;
    }

    public String getOriginalChargeCode() {
        return originalChargeCode;
    }

    public void setOriginalChargeCode(String originalChargeCode) {
        this.originalChargeCode = originalChargeCode;
    }

    public String getSunshineProductCode() {
        return sunshineProductCode;
    }

    public void setSunshineProductCode(String sunshineProductCode) {
        this.sunshineProductCode = sunshineProductCode;
    }

    public String getMedicalInsuranceCode() {
        return medicalInsuranceCode;
    }

    public void setMedicalInsuranceCode(String medicalInsuranceCode) {
        this.medicalInsuranceCode = medicalInsuranceCode;
    }

    public String getNationalLowestHistoricalSalesPrice() {
        return nationalLowestHistoricalSalesPrice;
    }

    public void setNationalLowestHistoricalSalesPrice(String nationalLowestHistoricalSalesPrice) {
        this.nationalLowestHistoricalSalesPrice = nationalLowestHistoricalSalesPrice;
    }

    public String getShangyaoErpMaterialCode() {
        return shangyaoErpMaterialCode;
    }

    public void setShangyaoErpMaterialCode(String shangyaoErpMaterialCode) {
        this.shangyaoErpMaterialCode = shangyaoErpMaterialCode;
    }

    public String getHighLowValueSubClassification() {
        return highLowValueSubClassification;
    }

    public void setHighLowValueSubClassification(String highLowValueSubClassification) {
        this.highLowValueSubClassification = highLowValueSubClassification;
    }

    public String getMainStorageZone() {
        return mainStorageZone;
    }

    public void setMainStorageZone(String mainStorageZone) {
        this.mainStorageZone = mainStorageZone;
    }

    public String getSunshineSpecModelNumber() {
        return sunshineSpecModelNumber;
    }

    public void setSunshineSpecModelNumber(String sunshineSpecModelNumber) {
        this.sunshineSpecModelNumber = sunshineSpecModelNumber;
    }

    public String getSunshineConversionRate() {
        return sunshineConversionRate;
    }

    public void setSunshineConversionRate(String sunshineConversionRate) {
        this.sunshineConversionRate = sunshineConversionRate;
    }

    public String getInsuranceCatalogLevel1() {
        return insuranceCatalogLevel1;
    }

    public void setInsuranceCatalogLevel1(String insuranceCatalogLevel1) {
        this.insuranceCatalogLevel1 = insuranceCatalogLevel1;
    }

    public String getInsuranceCatalogLevel2() {
        return insuranceCatalogLevel2;
    }

    public void setInsuranceCatalogLevel2(String insuranceCatalogLevel2) {
        this.insuranceCatalogLevel2 = insuranceCatalogLevel2;
    }

    public String getInsuranceCatalogLevel3() {
        return insuranceCatalogLevel3;
    }

    public void setInsuranceCatalogLevel3(String insuranceCatalogLevel3) {
        this.insuranceCatalogLevel3 = insuranceCatalogLevel3;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getManagementType() {
        return managementType;
    }

    public void setManagementType(String managementType) {
        this.managementType = managementType;
    }

    public String getSupervisionCategory() {
        return supervisionCategory;
    }

    public void setSupervisionCategory(String supervisionCategory) {
        this.supervisionCategory = supervisionCategory;
    }

    public String getSupervisionCategoryCode() {
        return supervisionCategoryCode;
    }

    public void setSupervisionCategoryCode(String supervisionCategoryCode) {
        this.supervisionCategoryCode = supervisionCategoryCode;
    }

    public String getIsCharged() {
        return isCharged;
    }

    public void setIsCharged(String isCharged) {
        this.isCharged = isCharged;
    }

    public String getHighLowValueClassification() {
        return highLowValueClassification;
    }

    public void setHighLowValueClassification(String highLowValueClassification) {
        this.highLowValueClassification = highLowValueClassification;
    }

    public String getDisposableSterilePackaging() {
        return disposableSterilePackaging;
    }

    public void setDisposableSterilePackaging(String disposableSterilePackaging) {
        this.disposableSterilePackaging = disposableSterilePackaging;
    }

    public String getIsOneItemOneCode() {
        return isOneItemOneCode;
    }

    public void setIsOneItemOneCode(String isOneItemOneCode) {
        this.isOneItemOneCode = isOneItemOneCode;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public String getIsImplanted() {
        return isImplanted;
    }

    public void setIsImplanted(String isImplanted) {
        this.isImplanted = isImplanted;
    }

    public String getIsInterventional() {
        return isInterventional;
    }

    public void setIsInterventional(String isInterventional) {
        this.isInterventional = isInterventional;
    }

    public String getIsBidWinning() {
        return isBidWinning;
    }

    public void setIsBidWinning(String isBidWinning) {
        this.isBidWinning = isBidWinning;
    }

    public String getIsExclusivePurchase() {
        return isExclusivePurchase;
    }

    public void setIsExclusivePurchase(String isExclusivePurchase) {
        this.isExclusivePurchase = isExclusivePurchase;
    }

    public String getUsageLevel() {
        return usageLevel;
    }

    public void setUsageLevel(String usageLevel) {
        this.usageLevel = usageLevel;
    }

    public String getIsEpidemicControlMaterial() {
        return isEpidemicControlMaterial;
    }

    public void setIsEpidemicControlMaterial(String isEpidemicControlMaterial) {
        this.isEpidemicControlMaterial = isEpidemicControlMaterial;
    }

    public String getDefaultStorageLocation() {
        return defaultStorageLocation;
    }

    public void setDefaultStorageLocation(String defaultStorageLocation) {
        this.defaultStorageLocation = defaultStorageLocation;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getFinancialClassification() {
        return financialClassification;
    }

    public void setFinancialClassification(String financialClassification) {
        this.financialClassification = financialClassification;
    }

    public String getMedicalCategoryClassification() {
        return medicalCategoryClassification;
    }

    public void setMedicalCategoryClassification(String medicalCategoryClassification) {
        this.medicalCategoryClassification = medicalCategoryClassification;
    }

    public String getSentToSunshinePlatform() {
        return sentToSunshinePlatform;
    }

    public void setSentToSunshinePlatform(String sentToSunshinePlatform) {
        this.sentToSunshinePlatform = sentToSunshinePlatform;
    }

    public String getSunshineSendTime() {
        return sunshineSendTime;
    }

    public void setSunshineSendTime(String sunshineSendTime) {
        this.sunshineSendTime = sunshineSendTime;
    }

    public String getLastInboundTime() {
        return lastInboundTime;
    }

    public void setLastInboundTime(String lastInboundTime) {
        this.lastInboundTime = lastInboundTime;
    }

    public String getLastOutboundTime() {
        return lastOutboundTime;
    }

    public void setLastOutboundTime(String lastOutboundTime) {
        this.lastOutboundTime = lastOutboundTime;
    }

    public String getHighValueGovernanceSerial() {
        return highValueGovernanceSerial;
    }

    public void setHighValueGovernanceSerial(String highValueGovernanceSerial) {
        this.highValueGovernanceSerial = highValueGovernanceSerial;
    }

    public String getGovernedConsumableName() {
        return governedConsumableName;
    }

    public void setGovernedConsumableName(String governedConsumableName) {
        this.governedConsumableName = governedConsumableName;
    }

    public String getSunshinePlatformSource() {
        return sunshinePlatformSource;
    }

    public void setSunshinePlatformSource(String sunshinePlatformSource) {
        this.sunshinePlatformSource = sunshinePlatformSource;
    }

    public String getMasterCatalogCode() {
        return masterCatalogCode;
    }

    public void setMasterCatalogCode(String masterCatalogCode) {
        this.masterCatalogCode = masterCatalogCode;
    }

    public String getExclusiveMachineUse() {
        return exclusiveMachineUse;
    }

    public void setExclusiveMachineUse(String exclusiveMachineUse) {
        this.exclusiveMachineUse = exclusiveMachineUse;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public String getServiceFeeRate() {
        return serviceFeeRate;
    }

    public void setServiceFeeRate(String serviceFeeRate) {
        this.serviceFeeRate = serviceFeeRate;
    }

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public String getSupplierRemark() {
        return supplierRemark;
    }

    public void setSupplierRemark(String supplierRemark) {
        this.supplierRemark = supplierRemark;
    }

    public String getFirstBatchGovernedConsumables() {
        return firstBatchGovernedConsumables;
    }

    public void setFirstBatchGovernedConsumables(String firstBatchGovernedConsumables) {
        this.firstBatchGovernedConsumables = firstBatchGovernedConsumables;
    }

    public String getIsCentralizedProcurement() {
        return isCentralizedProcurement;
    }

    public void setIsCentralizedProcurement(String isCentralizedProcurement) {
        this.isCentralizedProcurement = isCentralizedProcurement;
    }

    public String getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(String approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getOriginalHospitalSystemCode() {
        return originalHospitalSystemCode;
    }

    public void setOriginalHospitalSystemCode(String originalHospitalSystemCode) {
        this.originalHospitalSystemCode = originalHospitalSystemCode;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(String contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getContractDetailStartDate() {
        return contractDetailStartDate;
    }

    public void setContractDetailStartDate(String contractDetailStartDate) {
        this.contractDetailStartDate = contractDetailStartDate;
    }

    public String getContractDetailEndDate() {
        return contractDetailEndDate;
    }

    public void setContractDetailEndDate(String contractDetailEndDate) {
        this.contractDetailEndDate = contractDetailEndDate;
    }

    public String getHisUnit() {
        return hisUnit;
    }

    public void setHisUnit(String hisUnit) {
        this.hisUnit = hisUnit;
    }

    public String getHisPrice() {
        return hisPrice;
    }

    public void setHisPrice(String hisPrice) {
        this.hisPrice = hisPrice;
    }

    public String getHisConversionRatio() {
        return hisConversionRatio;
    }

    public void setHisConversionRatio(String hisConversionRatio) {
        this.hisConversionRatio = hisConversionRatio;
    }

    public String getIsSuspended() {
        return isSuspended;
    }

    public void setIsSuspended(String isSuspended) {
        this.isSuspended = isSuspended;
    }

    public String getIsStandardized() {
        return isStandardized;
    }

    public void setIsStandardized(String isStandardized) {
        this.isStandardized = isStandardized;
    }
}
