/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReorderLevel {

    private ReorderKey key;
    private Integer deptId;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private Double minQty;
    private String minUnitCode;
    private Double maxQty;
    private String maxUnitCode;
    private Double balQty;
    private String balUnit;
    private Double orderQty;
    private String orderUnitCode;
    private double minSmallQty;
    private double maxSmallQty;
    private double balSmallQty;
    private String status;
    private String locName;
    private Integer position;
}
