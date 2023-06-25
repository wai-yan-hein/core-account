/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.user.model.AppUser;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitPattern implements Serializable {

    private String patternCode;
    private String patternName;
    
    private AppUser updatedBy;
    private Date createdDate;
    private AppUser createdBy;
    private Integer macId;
    private String userCode;
    private String compCode;

}
