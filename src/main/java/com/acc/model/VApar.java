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
public class VApar {

    private VAparKey key;
    private String userCode;
    private String traderName;
    private Double drAmt;
    private Double crAmt;
}
