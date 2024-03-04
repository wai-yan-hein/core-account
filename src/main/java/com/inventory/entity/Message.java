package com.inventory.entity;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Message {

    private String header;
    private String entity;
    private String message;
    private String vouNo;
    private Map<String, Object> params;
    private Integer macId;
    private List<Integer> pageSize;
}
