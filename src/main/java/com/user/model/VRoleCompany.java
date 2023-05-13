/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class VRoleCompany implements java.io.Serializable {

    private String compCode;
    private String roleCode;
    private String compName;
    private String compPhone;
    private String compAddress;
    private Date startDate;
    private Date endDate;
    private String currency;
    private boolean batchLock;
    private Date yearEndDate;
}
