/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
/**
 *
 * @author DELL
 */
@Data
public class OrderHis implements java.io.Serializable{
    @NonNull
    private OrderHisKey key;
    private String traderCode;
    private String saleManCode;
    @NonNull
    private Date vouDate;
    private Date creditTerm;
    @NonNull
    private String curCode;
    private Float vouTotal;
    private Float grandTotal;
    private Float discount;
    private Float discP;
    private Float taxAmt;
    private Float taxPercent;
    private Boolean deleted;
    private Float paid;
    private Float balance;
    @NonNull
    private Date createdDate;
    @NonNull
    private String createdBy;
    private Integer session;
    private String updatedBy;
    private String address;
    private String orderCode;
    private String remark;
    private String reference;
    private Region region;
    private boolean vouLock;
    @NonNull
    private String locCode;
    @NonNull
    private Integer macId;
    @NonNull
    private String status = "STATUS";
    private List<OrderHisDetail> listSH;
    private List<OrderDetailKey> listDel;
    private boolean backup;
    private String projectNo;
     public OrderHis() {
    }
    
}
