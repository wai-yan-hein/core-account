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
public class CompanyInfo {

    private String compCode;
    private String userCode;
    private String compName;
    private String compAddress;
    private String compPhone;
    private String compEmail;
    private Date startDate;
    private Date endDate;
    private boolean active;
    private String curCode;
    private String createdBy;
    private Date createdDate;
    private Integer busId;
    private boolean batchLock;
    private Date yearEndDate;

    @Override
    public String toString() {
        return compName;
    }
    

}
