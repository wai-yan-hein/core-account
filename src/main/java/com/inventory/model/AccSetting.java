/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccSetting implements Serializable {

    private AccKey key;
    private String discountAcc;
    private String payAcc;
    private String taxAcc;
    private String sourceAcc;
    private String balanceAcc;
    private String commAcc;
    private String deptCode;
}
