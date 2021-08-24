/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Order implements Serializable {

    private String orderCode;
    private Date orderDate;
    private String desp;
    private Trader trader;
    private Boolean isOrder;
    private Float orderTotal;
    private Date updatedDate;
    private String orderAddres;

}
