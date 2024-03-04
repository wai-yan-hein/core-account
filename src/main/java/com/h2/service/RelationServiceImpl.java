/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.RelationDao;
import com.inventory.entity.RelationKey;
import com.inventory.entity.UnitRelation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Lazy
@Service
@Transactional
public class RelationServiceImpl implements RelationService {

    @Autowired
    private RelationDao dao;

    @Override
    public UnitRelation save(UnitRelation cat) {
        return dao.save(cat);
    }

    @Override
    public List<UnitRelation> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return dao.findByKey(key);
    }

}
