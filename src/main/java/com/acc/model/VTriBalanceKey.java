/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class VTriBalanceKey implements Serializable {

    private String coaCode;
    private String curCode;
    private Integer compCode;

}
