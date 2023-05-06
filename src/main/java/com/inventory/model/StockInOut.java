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

    private StockIOKey key;
    private String remark;
    private String description;
    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Integer macId;
    private String vouStatusCode;
    private Date vouDate;
    private Boolean deleted;
    private String status = "STATUS";
    private List<StockInOutDetail> listSH;
    private List<StockInOutKey> listDel;
}
