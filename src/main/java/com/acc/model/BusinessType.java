/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class BusinessType {

    private Integer busId;
    private String busName;

    @Override
    public String toString() {
        return busName;
    }

}
