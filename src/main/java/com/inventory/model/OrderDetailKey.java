/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class OrderDetailKey {

    private String compCode;
    private Integer deptId;
    private String vouNo;
    private Integer uniqueId;

}
