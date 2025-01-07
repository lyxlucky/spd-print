package com.spd.utils;

import com.spd.config.PrintConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaseUrlFactory {

    @Autowired
    private PrintConfig printConfig;

    private static final Map<String, String> HOSPITAL_URL_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
        HOSPITAL_URL_MAP.put("1", printConfig.getBdBaseUrl());
        HOSPITAL_URL_MAP.put("2", printConfig.getLgBaseUrl());
        HOSPITAL_URL_MAP.put("81", printConfig.getZqBaseUrl());
        HOSPITAL_URL_MAP.put("161", printConfig.getHnBaseUrl());
        HOSPITAL_URL_MAP.put("221", printConfig.getFyBaseUrl());
        HOSPITAL_URL_MAP.put("241", printConfig.getSmBaseUrl());
    }

    public String declareBaseUrl(String hospitalCode){
        return HOSPITAL_URL_MAP.get(hospitalCode);
    }

}
