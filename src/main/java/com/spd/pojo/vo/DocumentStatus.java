package com.spd.pojo.vo;

/**
 * 文档状态枚举
 * 0：新增
 * 1：更新
 * 9：删除
 */
public enum DocumentStatus {
    /**
     * 新增
     */
    CREATE(0),
    
    /**
     * 更新
     */
    UPDATE(1),
    
    /**
     * 删除
     */
    DELETE(9);

    private final int code;

    DocumentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}