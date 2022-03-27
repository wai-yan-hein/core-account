/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.user.model.Currency;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
public class RetInHis implements java.io.Serializable {

    private String vouNo;
    private Trader trader;
    private Date vouDate;
    private Location location;
    private Boolean deleted;
    private Float vouTotal;
    private Float paid;
    private Float discount;
    private Float balance;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String remark;
    private Integer session;
    private Currency currency;
    private Float discP;
    private String intgUpdStatus;
    private Integer macId;
    private String compCode;
    private String status = "STATUS";
    private List<RetInHisDetail> listRD;
    private List<String> listDel;

}
