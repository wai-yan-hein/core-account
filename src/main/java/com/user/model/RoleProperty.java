/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RoleProperty {

    private RolePropertyKey key;
    private String propValue;
    private String remark;

    public RoleProperty() {
    }

    public RoleProperty(RolePropertyKey key, String propValue) {
        this.key = key;
        this.propValue = propValue;
    }

}
