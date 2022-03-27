/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Pattern {

    private String patternCode;
    private String patternName;
    private VouStatus vouStatus;
    private String userCode;
    private String compCode;
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;
    private boolean active;
    private Integer macId;
    private List<PatternDetail> detailList;
    public Pattern() {
    }

    public Pattern(String patternName, boolean active) {
        this.patternName = patternName;
        this.active = active;
    }

}
