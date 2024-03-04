/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author DELL
 */
@Data
@Embeddable
public class RegionKey implements Serializable {

    @Column(name = "reg_code")
    private String regCode;
    @Column(name = "comp_code")
    private String compCode;

    public RegionKey() {
    }
    

    public RegionKey(String regCode, String compCode) {
        this.regCode = regCode;
        this.compCode = compCode;
    }
    
}
