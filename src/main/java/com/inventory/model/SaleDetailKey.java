/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;


/**
 *
 * @author wai yan
 */
@Data
@Embeddable
public class SaleDetailKey implements Serializable {
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "vou_no")
    private String vouNo;


}
