package com.inventory.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StockPaymentDetail {

    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private LocalDate refDate;
    private String stockUserCode;
    private String stockName;
    private String stockCode;
    private String refNo;
    private String remark;
    private String reference;
    private boolean fullPaid;
    private String projectNo;
    private double qty;
    private double payQty;
    private double balQty;
    private double bag;
    private double payBag;
    private double balBag;

}
