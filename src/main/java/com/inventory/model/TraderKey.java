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
public class TraderKey implements Serializable {

    @Column(name = "code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;

    public TraderKey() {
    }

    public TraderKey(String code, String compCode, Integer deptId) {
        this.code = code;
        this.compCode = compCode;
        this.deptId = deptId;
    }

}
