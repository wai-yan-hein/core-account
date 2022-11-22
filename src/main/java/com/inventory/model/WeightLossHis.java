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
 * @author DELL
 */
@Data
public class WeightLossHis {

    private WeightLossHisKey key;
    private Date vouDate;
    private String refNo;
    private String remark;
    private String updatedBy;
    private String createdBy;
    private boolean deleted;
    private Date updatedDate;
    private Integer macId;
    private List<WeightLossDetail> listDetail;
    private List<WeightLossDetailKey> delKeys;
}
