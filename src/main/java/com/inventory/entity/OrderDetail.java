/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetail implements Serializable {

    private String id;
    private Stock stock;
    private Float qty;
    private Float price;
    private Float amount;
    private int uniqueId;
    private String orderCode;
    

}
