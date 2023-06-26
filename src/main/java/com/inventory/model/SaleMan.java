/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Entity
@Table(name = "sale_man")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleMan {

    @EmbeddedId
    private SaleManKey key;
    @Column(name = "saleman_name", unique = true, nullable = false)
    private String saleManName;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "phone")
    private String phone;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "dept_id")
    private Integer deptId;

    public SaleMan() {
    }

    public SaleMan(String saleManCode, String saleManName) {
        this.key = new SaleManKey();
        this.key.setSaleManCode(saleManCode);
        this.saleManName = saleManName;
    }

}
