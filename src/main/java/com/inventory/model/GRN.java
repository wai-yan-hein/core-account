/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRN {

    private GRNKey key;
    private String batchNo;
    private Date vouDate;
    private String traderCode;
    private String traderUserCode;
    private String traderName;
    private boolean closed;
    private boolean deleted;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String updatedBy;
    private String remark;
    private List<GRNDetail> listDetail;
    private List<GRNDetailKey> listDel;
    private String locCode;

    public GRN() {
    }

    public GRN(String batchNo) {
        this.batchNo = batchNo;
    }

}
