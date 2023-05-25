/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.UnitRelation;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RelationDao {

    UnitRelation save(UnitRelation stock);

    String getMaxDate();

    List<UnitRelation> findAll(String compCode);

}
