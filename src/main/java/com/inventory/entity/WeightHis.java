package com.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "weight_his")
public class WeightHis {

    @EmbeddedId
    private WeightHisKey key;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "description")
    private String description;
    @Column(name = "dept_id")
    private int deptId;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "weight")
    private double weight;
    @Column(name = "total_qty")
    private double totalQty;
    @Column(name = "total_weight")
    private double totalWeight;
    @Column(name = "total_bag")
    private double totalBag;
    @Column(name = "remark")
    private String remark;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "draft")
    private boolean draft;
    @Column(name = "post")
    private boolean post;
    @Transient
    private List<WeightHisDetail> listDetail;
    @Transient
    private ZonedDateTime vouDateTime;
    @Transient
    private String stockUserCode;
    @Transient
    private String stockName;
    @Transient
    private String traderUserCode;
    @Transient
    private String traderName;
}
