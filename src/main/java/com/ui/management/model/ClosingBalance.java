package com.ui.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClosingBalance {

    private String typeUserCode;
    private String typeName;
    private String stockUsrCode;
    private String stockCode;
    private String stockName;
    private String vouNo;
    private String vouDate;
    private double openQty;
    private double openAmt;
    private String openRel;
    private double purQty;
    private double purAmt;
    private String purRel;
    private double inQty;
    private double inAmt;
    private String inRel;
    private double outQty;
    private double outAmt;
    private String outRel;
    private double saleQty;
    private double saleAmt;
    private String saleRel;
    private double balQty;
    private double closingAmt;
    private String balRel;

    private double openWeight;
    private String openWeightRel;
    private double purWeight;
    private String purWeightRel;
    private double inWeight;
    private String inWeightRel;
    private double outWeight;
    private String outWeightRel;
    private double saleWeight;
    private String saleWeightRel;
    private double balWeight;
    private String balWeightRel;

    private String groupName;
    private String remark;
    private String compCode;
    private Integer deptId;
    private String relCode;
    private String weightUnit;
    private String traderCode;
    private String traderUserCode;
    private String traderName;
    private String locName;
    private String unitName;
    private double weight;
    private double totalQty;
}
