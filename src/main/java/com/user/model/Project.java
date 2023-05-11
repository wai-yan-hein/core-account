/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Project {

    private ProjectKey key;
    private String projectName;
    private Date startDate;
    private Date endDate;
    private Double budget;
    private String projectStatus;

    public Project() {
    }
    

    public Project(ProjectKey key, String projectName) {
        this.key = key;
        this.projectName = projectName;
    }

}
