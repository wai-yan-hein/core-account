/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetOutKey {

    private String vouNo;
    private int uniqueId;
    private String compCode;

}
