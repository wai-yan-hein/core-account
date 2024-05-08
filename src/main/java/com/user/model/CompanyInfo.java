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
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDate startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;
    @Column(name = "active")
    private boolean active;
    @Column(name = "currency")
    private String curCode;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "bus_id")
    private Integer busId;
    @Column(name = "batch_lock")
    private boolean batchLock;
    @Column(name = "year_end_date")
    private LocalDate yearEndDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "security_code")
    private String securityCode;
    @Column(name = "sync")
    private boolean sync;
    @Column(name = "report_company")
    private String reportCompany;
    @Column(name = "report_url")
    private String reportUrl;
    @Column(name = "order_id")
    private Integer orderId;
    @Transient
    private String token;
    @Transient
    private Boolean updateMenu;
    @Transient
    private String roleCode;
    @Transient
    private boolean allow;

    @Override
    public String toString() {
        return compName;
    }

}
