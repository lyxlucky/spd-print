package com.spd.pojo.vo;

import java.util.Objects;

public class ResponseVO {

    private int code;

    private String msg;

    private Object data;

    public ResponseVO() {
    }

    public ResponseVO(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseVO Success(Object data){
        return new ResponseVO(200,"处理成功",data);
    }

    public static ResponseVO Success(){
        return Success(null);
    }

    public static ResponseVO Error(String msg){
        return new ResponseVO(201,msg,null);
    }

    public static ResponseVO Error(Object data){
        return new ResponseVO(201,"处理失败",data);
    }

    public static ResponseVO Error(){
        return Error(null);
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResponseVO that = (ResponseVO) o;
        return code == that.code && Objects.equals(msg, that.msg) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code, msg, data);
    }
}
