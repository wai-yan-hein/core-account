/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class TraderA {

    private String code;
    private String compCode;
    private String traderName;
    private String traderType;
    private String address;
    private String regCode;
    private String phone;
    private String email;
    private String accCode;
    private boolean active;
    private String remark;
    private String parent;
    private String appShortName;
    private String appTraderCode;
    private String migCode;
    private Date updatedDate;
    private String updatedUser;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public TraderA(String code, String traderName) {
        this.code = code;
        this.traderName = traderName;
    }

    public TraderA() {
    }

}
