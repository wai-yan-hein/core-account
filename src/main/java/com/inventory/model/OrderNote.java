package com.inventory.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    List<OrderFileJoin> detailList;
}
