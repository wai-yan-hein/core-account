/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.RelationKey;
import com.inventory.entity.UnitRelation;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RelationDao {

    UnitRelation save(UnitRelation stock);

    UnitRelation findByKey(RelationKey key);

    String getMaxDate();

    List<UnitRelation> findAll(String compCode);

}
