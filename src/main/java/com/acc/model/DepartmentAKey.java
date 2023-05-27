/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Embeddable
@Data
public class DepartmentAKey implements Serializable{

    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "comp_code")
    private String compCode;

}
