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
 * @author winswe
 */
@Data
public class VAparKey implements Serializable {

    private String traderCode;
    private Integer macId;
    private String compCode;
    private String curCode;
    private String coaCode;
    private String deptCode;

}
