/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
public class RetOutHis implements java.io.Serializable {

    private String vouNo;
    private Trader trader;
    private Date vouDate;
    private Location location;
    private Boolean deleted;
    private Float vouTotal;
    private Float paid;
    private Float discount;
    private Float balance;
    private AppUser createdBy;
    private Date createdDate;
    private AppUser updatedBy;
    private Date updatedDate;
    private String remark;
    private Integer session;
    private Currency currency;
    private Float discP;
    private String intgUpdStatus;
    private Integer macId;
    private String compCode;
    private String status = "STATUS";
    private List<RetOutHisDetail> listRD;
    private List<String> listDel;
}
