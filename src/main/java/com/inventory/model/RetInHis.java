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

    private RetInHisKey key;
    private String traderCode;
    private Date vouDate;
    private String locCode;
    private Boolean deleted;
    private Float vouTotal;
    private Float paid;
    private Float discount;
    private Float balance;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private String remark;
    private Integer session;
    private String curCode;
    private Float discP;
    private Integer macId;
    private String status = "STATUS";
    private List<RetInHisDetail> listRD;
    private List<String> listDel;
    private boolean vouLock;

}
