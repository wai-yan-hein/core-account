/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public class TransferHis {

    private TransferHisKey key;
    private String createdBy;
    private Date createdDate;
    private boolean deleted;
    private Date vouDate;
    private String refNo;
    private String remark;
    private String updatedBy;
    private String locCodeFrom;
    private String locCodeTo;
    private Integer macId;
    private List<TransferHisDetail> listTD;
    private List<String> delList;
    private String status;
}
