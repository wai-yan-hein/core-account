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
import jakarta.persistence.Transient;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "ret_in_his")
public class RetInHis {

    @EmbeddedId
    private RetInHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "balance")
    private Float balance;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "project_no")
    private String projectNo;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<RetInHisDetail> listRD;
    @Transient
    private List<RetInKey> listDel;
    @Transient
    private List<String> location;
}
