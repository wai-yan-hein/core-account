/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class StockUnitKey implements Serializable{

    @Column(name = "unit_code")
    private String unitCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
}
