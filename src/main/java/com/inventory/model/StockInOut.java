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
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "stock_in_out")
public class StockInOut implements Serializable {

    @EmbeddedId
    private StockIOKey key;
    @Column(name = "remark")
    private String remark;
    @Column(name = "description")
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "vou_status")
    private String vouStatusCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<StockInOutDetail> listSH;
    @Transient
    private List<StockInOutKey> listDel;
    @Transient
    private List<LocationKey> keys;
}
