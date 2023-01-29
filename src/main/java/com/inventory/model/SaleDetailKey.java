/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;


/**
 *
 * @author wai yan
 */
@Data
public class SaleDetailKey implements Serializable {
    private String compCode;
    private String vouNo;
    private String sdCode;
    private Integer uniqueId;

}
