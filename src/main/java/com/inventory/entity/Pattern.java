package com.inventory.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;

/**
 *
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pattern")
@Data
public class Pattern implements java.io.Serializable {

    @EmbeddedId
    private PatternKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "qty")
    private double qty;
    @Column(name = "price")
    private double price;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "price_type")
    private String priceTypeCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Transient
    private String priceTypeName;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relation;
    @Transient
    private String locName;
    @Transient
    private double amount;

}
