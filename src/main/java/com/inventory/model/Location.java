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
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "location")
public class Location {

    @EmbeddedId
    private LocationKey key;
    @Column(name = "loc_name")
    private String locName;
    @Column(name = "parent")
    private String parentCode;
    @Column(name = "calc_stock")
    private boolean calcStock;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "map_dept_id")
    private Integer mapDeptId;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "cash_acc")
    private String cashAcc;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "active")
    private boolean active;

    public Location(String locCode, String locName) {
        this.key = new LocationKey();
        this.key.setLocCode(locCode);
        this.locName = locName;
    }

    public Location() {
    }

    @Override
    public String toString() {
        return locName;
    }

}
