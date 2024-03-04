/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.entity.RelationKey;
import com.inventory.entity.UnitRelation;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RelationService {

    UnitRelation save(UnitRelation stock);

    UnitRelation findByKey(RelationKey key);

    List<UnitRelation> findAll(String compCode);

    String getMaxDate();

}
