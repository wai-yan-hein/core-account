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
public class OrderDetail implements Serializable {

    private String id;
    private Stock stock;
    private Float qty;
    private Float price;
    private Float amount;
    private Integer uniqueId;
    private String orderCode;
    

}
