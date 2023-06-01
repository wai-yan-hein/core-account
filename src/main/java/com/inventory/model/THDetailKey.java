/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Embeddable
public class THDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;

}
