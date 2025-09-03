package com.spd.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PekingPaperlessDTO {
    private Map<String, List<Paperless>> body;
    private String type;
}

