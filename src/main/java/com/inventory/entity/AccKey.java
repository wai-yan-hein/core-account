/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author pann
 */
@Data
@Embeddable
public class AccKey implements Serializable{
    
    @Column(name = "type")
    private String type;
    @Column(name = "comp_code")
    private String compCode;
      public AccKey() {

    }
    public AccKey(String type, String compCode) {
        this.type = type;
        this.compCode = compCode;
    }

}
