/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class OPHis {

    private OPHisKey key;
    private Date vouDate;
    private String vouDateStr;
    private String curCode;
    private String remark;
    private String createdBy;
    private String locCode;
    private String locName;
    private Date createdDate;
    private String updatedBy;
    
    private boolean deleted;
    private Integer macId;
    private float opAmt;
    private List<OPHisDetail> detailList;
    private List<String> listDel;
    private String status = "STATUS";

}
