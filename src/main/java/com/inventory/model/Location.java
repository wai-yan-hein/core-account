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
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
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
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "map_dept_id")
    private Integer mapDeptId;

    public Location(String locCode, String locName) {
        this.key = new LocationKey();
        this.key.setLocCode(locCode);
        this.locName = locName;
    }

    public Location() {
    }

}
