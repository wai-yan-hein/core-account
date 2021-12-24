/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class StockInOut implements Serializable {

    private String vouNo;
    private String remark;
    private String description;
    private Date updatedDate;
    private AppUser createdBy;
    private AppUser updatedBy;
    private Date createdDate;
    private String compCode;
    private Integer macId;
    private VouStatus vouStatus;
    private Date vouDate;
    private Boolean deleted;
    private String status = "STATUS";
    private List<StockInOutDetail> listSH;
    private List<String> listDel;
}
