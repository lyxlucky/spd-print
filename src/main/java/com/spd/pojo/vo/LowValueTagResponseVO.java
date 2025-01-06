package com.spd.pojo.vo;

import java.util.List;

public class LowValueTagResponseVO {

    private int code;
    private String msg;
    private List<LowValueTagVO> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<LowValueTagVO> getData() {
        return data;
    }

    public void setData(List<LowValueTagVO> data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "LowValueTagResponseVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
