/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Department {

    private String deptCode;
    private String deptName;
    private String parentDept;
    private boolean active;
    private String compCode;
    private String createdBy;
    private Date createdDt;
    private String updatedBy;
    private Date updatedDt;
    private String userCode;
    private Integer macId;
    private List<Department> child;

    public Department(String deptCode, String deptName) {
        this.deptCode = deptCode;
        this.deptName = deptName;
    }

    public Department() {
    }


    @Override
    public String toString() {
        return deptName;
    }
}
