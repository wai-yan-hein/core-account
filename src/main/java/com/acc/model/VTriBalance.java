/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VTriBalance implements java.io.Serializable {

    private String coaCode;
    private String curCode;
    private String compCode;
    private double drAmt;
    private double crAmt;
    private String coaName;
    private String coaUsrCode;
    private Integer macId;

}
