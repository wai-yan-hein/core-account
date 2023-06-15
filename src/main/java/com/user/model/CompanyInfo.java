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
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "company_info")
public class CompanyInfo {

    @Id
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "name")
    private String compName;
    @Column(name = "address")
    private String compAddress;
    @Column(name = "phone")
    private String compPhone;
    @Column(name = "email")
    private String compEmail;
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "active")
    private boolean active;
    @Column(name = "currency")
    private String curCode;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "bus_id")
    private Integer busId;
    @Column(name = "batch_lock")
    private boolean batchLock;
    @Column(name = "year_end_date")
    private Date yearEndDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;

    @Override
    public String toString() {
        return compName;
    }

}
