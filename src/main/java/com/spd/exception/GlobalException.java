package com.spd.exception;

import com.spd.pojo.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseVO exceptionHandler(Exception e){
        return ResponseVO.Error(e.getMessage());
    }

}
