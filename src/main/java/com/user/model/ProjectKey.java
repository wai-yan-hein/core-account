/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class ProjectKey implements Serializable {

    @Column(name = "project_code")
    private String projectCode;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "comp_code")
    private String compCode;

    public ProjectKey(String projectNo, String compCode) {
        this.projectNo = projectNo;
        this.compCode = compCode;
    }

    public ProjectKey() {
    }

}
