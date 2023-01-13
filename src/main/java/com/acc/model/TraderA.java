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

    private TraderAKey key;
    private String userCode;
    private String traderName;
    private String traderType;
    private String address;
    private String phone;
    private String email;
    private String account;
    private boolean active;
    private String remark;
    private String migCode;
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;

    public TraderA(TraderAKey key, String traderName) {
        this.key = key;
        this.traderName = traderName;
    }

    public TraderA() {
    }

}
