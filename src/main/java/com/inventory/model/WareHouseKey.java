package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class WareHouseKey implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;

    public WareHouseKey() {
    }
   
    public WareHouseKey(String code, String compCode) {
        this.code = code;
        this.compCode = compCode;
    }
}
