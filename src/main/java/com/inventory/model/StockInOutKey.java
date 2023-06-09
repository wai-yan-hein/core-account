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
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class StockInOutKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
}
