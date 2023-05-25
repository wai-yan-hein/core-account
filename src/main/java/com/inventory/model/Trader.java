/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

/**
 *
 * Trader class is parent class of Customer, Patient and Supplier class. Sharing
 * "trader" table with Patient, Customer and Supplier class. Database table name
 * is trader.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "trader")
public class Trader {

    @EmbeddedId
    private TraderKey key;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "address")
    private String address;
    @Column(name = "reg_code")
    private String regCode;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "active")
    private boolean active;
    @Column(name = "remark")
    private String remark;
    @Column(name = "rfid")
    private String rfId;
    @Column(name = "nrc")
    private String nrc;
    @Column(name = "mig_code")
    private String migCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "credit_days")
    private Integer creditLimit;
    @Column(name = "credit_limit")
    private Integer creditDays;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "type")
    private String type;
    @Column(name = "cash_down")
    private boolean cashDown;
    @Column(name = "multi")
    private boolean multi;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "price_type")
    private String priceType;
    @Column(name = "group_code")
    private String groupCode;
    @Column(name = "account")
    private String account;
    @Column(name = "deleted")
    private boolean deleted;

    public Trader(String code, String traderName) {
        this.key = new TraderKey();
        this.key.setCode(code);
        this.traderName = traderName;
    }

    public Trader() {
    }
}
