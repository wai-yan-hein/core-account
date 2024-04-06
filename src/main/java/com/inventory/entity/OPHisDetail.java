/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OPHisDetail {

    private OPHisDetailKey key;
    private Integer deptId;
    private String stockCode;
    private double qty;
    private double price;
    private double amount;
    private String locCode;
    private String unitCode;
    private double weight;
    private String weightUnit;
    private double totalWeight;
    private double wet;
    private double rice;
    private double bag;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
}
