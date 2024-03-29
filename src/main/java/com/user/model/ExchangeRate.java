/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import com.common.Global;
import com.common.Util1;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {

    @EmbeddedId
    @Column(name = "ex_code")
    private ExchangeKey key;
    @Column(name = "ex_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime exDate;
    @Column(name = "home_factor")
    private Double homeFactor;
    @Column(name = "home_cur")
    private String homeCur;
    @Column(name = "target_factor")
    private Double targetFactor;
    @Column(name = "target_cur")
    private String targetCur;
    @Transient
    private Double exRate;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "deleted")
    private boolean deleted;

    @Override
    public String toString() {
        String date = Util1.toDateStr(exDate, Global.dateFormat);
        String hf = Util1.toFormatPattern(homeFactor);
        String tf = Util1.toFormatPattern(targetFactor);
        String format = String.format("%s - %s %s -> %s %s", date, hf, homeCur, tf, targetCur);
        return format;
    }

}
