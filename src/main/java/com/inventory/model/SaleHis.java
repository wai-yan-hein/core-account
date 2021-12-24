/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Data
public class SaleHis implements java.io.Serializable {

    @NonNull
    private String vouNo;
    private Trader trader;
    private SaleMan saleMan;
    @NonNull
    private Date vouDate;
    private Date creditTerm;
    @NonNull
    private Currency currency;
    private Float vouTotal;
    private Float grandTotal;
    private Float discount;
    private Float discP;
    private Float taxAmt;
    private Float taxP;
    private Boolean deleted;
    private Float paid;
    private Float balance;
    @NonNull
    private Date createdDate;
    @NonNull
    private AppUser createdBy;
    private Integer session;
    private AppUser updatedBy;
    private Date updatedDate;
    private String address;
    private String orderCode;
    private String remark;
    private Region region;
    @NonNull
    private Location location;
    @NonNull
    private Integer macId;
    @NonNull
    private String compCode;
    @NonNull
    private String status = "STATUS";
    private List<SaleHisDetail> listSH;
    private List<String> listDel;

    public SaleHis() {
    }
}
