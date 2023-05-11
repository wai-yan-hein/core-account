/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class ProjectKey {
    private String projectNo;
    private String compCode;

    public ProjectKey(String projectNo, String compCode) {
        this.projectNo = projectNo;
        this.compCode = compCode;
    }

    public ProjectKey() {
    }
    
}
