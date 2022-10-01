/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Resolution {

    private int width;
    private int height;

    public Resolution() {
    }

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
}
