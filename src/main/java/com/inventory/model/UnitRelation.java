/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class UnitRelation implements Serializable {

    private String relCode;
    private String relName;
    private List<UnitRelationDetail> detailList;

    public UnitRelation(String relCode, String relName) {
        this.relCode = relCode;
        this.relName = relName;
    }

    public UnitRelation() {
    }
}
