package com.spd.controller;

import com.spd.pojo.vo.ResponseVO;

public class BaseController {

    public ResponseVO success(){
        return ResponseVO.Success();
    }

    public ResponseVO success(Object data){
        return ResponseVO.Success(data);
    }

    public ResponseVO error(String msg){
        return ResponseVO.Error(msg);
    }

    public ResponseVO error(Object data){
        return ResponseVO.Error(data);
    }

    public ResponseVO error(){
        return ResponseVO.Error();
    }

}
