/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
/**
 *
 * @author DELL
 */
@Data
@Embeddable
public class COAKey implements Serializable{

    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "comp_code")
    private String compCode;

    public COAKey(String coaCode, String compCode) {
        this.coaCode = coaCode;
        this.compCode = compCode;
    }

    public COAKey() {
    }
    
}

