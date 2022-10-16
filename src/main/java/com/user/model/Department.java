/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class Department {

    private Integer deptId;
    private String userCode;
    private String deptName;
    private String queueName;

    public Department(Integer deptId) {
        this.deptId = deptId;
    }

    public Department() {
    }
    

}
