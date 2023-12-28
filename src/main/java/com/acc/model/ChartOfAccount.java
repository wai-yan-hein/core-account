package com.acc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author dell
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@Entity
@Table(name = "chart_of_account")
public class ChartOfAccount {

    @EmbeddedId
    private COAKey key;
    @Column(name = "coa_name_eng")
    private String coaNameEng;
    @Column(name = "coa_name_mya")
    private String coaNameMya;
    @Column(name = "active")
    private boolean active;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "modify_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String modifiedBy;
    @Column(name = "coa_parent")
    private String coaParent;
    @Column(name = "coa_option")
    private String option;
    @Column(name = "coa_level")
    private Integer coaLevel;
    @Column(name = "coa_code_usr")
    private String coaCodeUsr;
    @Column(name = "parent_usr_code")
    private String parentUsrCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "marked")
    private boolean marked;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "mig_code")
    private String migCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "credit")
    private boolean credit;
    @Column(name = "sort_order_id")
    private Integer sortOrderId;
    @Column(name = "bank_no")
    private String bankNo;
    @Transient
    private String groupCode;
    @Transient
    private String groupUsrCode;
    @Transient
    private String groupName;
    @Transient
    private String headCode;
    @Transient
    private String headUsrCode;
    @Transient
    private String headName;
    @Transient
    private List<ChartOfAccount> child;

    public ChartOfAccount(COAKey key, String coaNameEng) {
        this.key = key;
        this.coaNameEng = coaNameEng;
    }

    public ChartOfAccount() {
    }

    @Override
    public String toString() {
        return coaNameEng;
    }
}
