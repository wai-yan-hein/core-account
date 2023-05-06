/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RetInHisDetail implements java.io.Serializable {

    private RetInKey key;
    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private Float qty;
    private Float avgQty;
    private String unitCode;
    private Float price;
    private Float amount;
    private String locCode;
    private String locName;
    private Integer uniqueId;

}
