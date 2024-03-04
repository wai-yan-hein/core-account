package com.inventory.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class OrderNote {

    String vouNo;
    String compCode;
    Integer deptId;
    Integer macId;
    String traderCode;
    String stockCode;
    String orderName;
    String orderCode;
    LocalDateTime vouDate;
    LocalDateTime createdDate;
    String createdBy;
    LocalDateTime updatedDate;
    String updatedBy;
    Boolean deleted;
    private ZonedDateTime vouDateTime;

    private String traderName;
    private String stockName;
    List<OrderFileJoin> detailList;
}
