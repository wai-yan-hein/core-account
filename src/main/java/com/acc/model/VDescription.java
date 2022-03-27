/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class VDescription implements Serializable {

    private String description;

    public VDescription(String description) {
        this.description = description;
    }

    public VDescription() {
    }



}
