/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * Trader class is parent class of Customer, Patient and Supplier class. Sharing
 * "trader" table with Patient, Customer and Supplier class. Database table name
 * is trader.
 */
@Data
public class Trader implements java.io.Serializable {

    private String code;
    private String compCode;
    private String traderName;
    private String address;
    private Region region;
    private String phone;
    private String email;
    private boolean active;
    private String remark;
    private String parent;
    private String appShortName; //use integration with other application
    private String appTraderCode; //Original trader id from integration app
    private String migCode;
    private Date updatedDate;
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private Integer creditLimit;
    private Integer creditDays;
    private String contactPerson;
    private String type;
    private String account;
    private boolean cashDown;
    private boolean multi;
    private String priceType;


    public Trader(String code, String traderName) {
        this.code = code;
        this.traderName = traderName;
    }

    public Trader() {
    }
}
