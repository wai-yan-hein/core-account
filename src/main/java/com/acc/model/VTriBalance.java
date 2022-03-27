/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import lombok.Data;

/**
 *
 * @author winswe
 */
@Data
public class VTriBalance implements java.io.Serializable {

    private VTriBalanceKey key;
    private Double drAmt;
    private Double crAmt;
    private String coaName;
    private String usrCoaCode;
    private Integer macId;

}
