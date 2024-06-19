/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.dto;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class PaymentType {
    private int id;
    private String type;

    public PaymentType(int id, String type) {
        this.id = id;
        this.type = type;
    }
    
}
