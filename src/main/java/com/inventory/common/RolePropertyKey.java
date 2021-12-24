/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RolePropertyKey implements Serializable {

    private String roleCode;
    private String propKey;

    public RolePropertyKey(String roleCode, String propKey) {
        this.roleCode = roleCode;
        this.propKey = propKey;
    }

}
