/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.user.model.Currency;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author wai yan
 */
@Data
public class SaleHis implements java.io.Serializable {

    @NonNull
    private SaleHisKey key;
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
    private Date updatedDate;
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
    private List<SaleHisDetail> listSH;
    private List<String> listDel;
    private boolean backup;

    public SaleHis() {
    }
}
