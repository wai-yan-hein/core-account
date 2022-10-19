/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class SysProperty {

    private PropertyKey key;
    private String propValue;
    private String remark;
}
