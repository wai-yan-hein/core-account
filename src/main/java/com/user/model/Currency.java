/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "currency")
public class Currency {

    @Id
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "cur_name")
    private String currencyName;
    @Column(name = "cur_symbol")
    private String currencySymbol;
    @Column(name = "active")
    private boolean active;
    @Column(name = "cur_gain_acc")
    private String curGainAcc;
    @Column(name = "cur_lost_acc")
    private String curLostAcc;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    public Currency(String curCode, String currencyName) {
        this.curCode = curCode;
        this.currencyName = currencyName;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return curCode;
    }

}
