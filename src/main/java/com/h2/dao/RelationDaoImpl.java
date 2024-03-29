/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.RelationKey;
import com.inventory.entity.UnitRelation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class RelationDaoImpl extends AbstractDao<RelationKey, UnitRelation> implements RelationDao {

    @Override
    public UnitRelation save(UnitRelation cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from UnitRelation o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<UnitRelation> findAll(String compCode) {
        String hsql = "select o from UnitRelation o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return getByKey(key);
    }

}
