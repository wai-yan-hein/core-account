/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class ProcessHis {

    private ProcessHisKey key;
    private String stockUsrCode;
    private String stockName;
    private Date vouDate;
    private Date endDate;
    private String ptCode;
    private String ptName;
    private String processNo;
    private String remark;
    private Float qty;
    private Float avgQty;
    private String unit;
    private Float price;
    private boolean finished;
    private boolean deleted;
    private Integer macId;
    private String cratedBy;
    private String updatedBy;
}
