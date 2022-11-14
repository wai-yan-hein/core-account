/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author myohtut
 */
@Data
public class OpeningBalance {

    private Integer opId;
    private Date opDate;
    private String sourceAccId;
    private String sourceAccName;
    private String currCode;
    private Double crAmt;
    private Double drAmt;
    private String uerCode;
    private String compCode;
    private Date createdDate;
    private String deptCode;
    private String traderCode;
    private String traderName;
    private String tranSource;
    private String depUserCode;
    private String tradeUserCode;
    private String traderType;
    private String coaUserCode;
    private String coaParent;
    private String regCode;
    
    
    public OpeningBalance(){
        
    }
}
