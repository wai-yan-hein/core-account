/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "trader_acco")
public class TraderA {

    @EmbeddedId
    private TraderAKey key;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "discriminator")
    private String traderType;
    @Column(name = "address")
    private String address;
    @Column(name = "reg_code")
    private String regCode;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "account_code")
    private String account;
    @Column(name = "active")
    private boolean active;
    @Column(name = "remark")
    private String remark;
    @Column(name = "mig_code")
    private String migCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "deleted")
    private boolean deleted;

    public TraderA(TraderAKey key, String traderName) {
        this.key = key;
        this.traderName = traderName;
    }

    public TraderA() {
    }

}
