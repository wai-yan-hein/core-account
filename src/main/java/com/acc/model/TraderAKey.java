/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import jakarta.persistence.Column;
import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraderAKey implements Serializable{

    @Column(name = "code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;

    public TraderAKey() {
    }

    public TraderAKey(String code, String compCode) {
        this.code = code;
        this.compCode = compCode;
    }
    
}
