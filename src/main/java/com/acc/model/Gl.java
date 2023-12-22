/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;

/**
 *
 * @author dell
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "gl")
public class Gl implements Cloneable {

    @EmbeddedId
    private GlKey key;
    @Column(name = "gl_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime glDate;
    @Column(name = "description")
    private String description;
    @Column(name = "source_ac_id")
    private String srcAccCode;
    @Column(name = "account_id")
    private String accCode;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "dr_amt")
    private double drAmt;
    @Column(name = "cr_amt")
    private double crAmt;
    @Column(name = "reference")
    private String reference;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "voucher_no")
    private String vouNo;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "modify_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifyDate;
    @Column(name = "modify_by")
    private String modifyBy;
    @Column(name = "user_code")
    private String createdBy;
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "gl_vou_no")
    private String glVouNo;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "remark")
    private String remark;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "from_des")
    private String fromDes;
    @Column(name = "for_des")
    private String forDes;
    @Column(name = "narration")
    private String narration;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "ex_code")
    private String exCode;
    @Column(name = "order_id")
    private Integer orderId;
    @Transient
    private String glDateStr;
    @Transient
    private List<GlKey> delList;
    @Transient
    private boolean cash = false;
    @Transient
    private String deptUsrCode;
    @Transient
    private String traderName;
    @Transient
    private String srcAccName;
    @Transient
    private String srcUserCode;
    @Transient
    private String accName;
    @Transient
    private String vouDate;
    @Transient
    private boolean edit;
    @Transient
    private Double opening;
    @Transient
    private Double closing;
    @Transient
    private Double amount;
    @Transient
    private String event;
    @Transient
    private boolean tranLock;

    public Gl(String curCode, Double drAmt, Double crAmt) {
        this.curCode = curCode;
        this.drAmt = drAmt;
        this.crAmt = crAmt;
    }

    public Gl() {
    }

    public Gl(String tranSource) {
        this.tranSource = tranSource;
    }

    @Override
    public Gl clone() {
        try {
            Gl clonedGl = (Gl) super.clone();
            return clonedGl;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Gl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
