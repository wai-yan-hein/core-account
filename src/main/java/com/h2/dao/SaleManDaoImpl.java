/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.SaleMan;
import com.inventory.model.SaleManKey;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class SaleManDaoImpl extends AbstractDao<SaleManKey, SaleMan> implements SaleManDao {

    @Override
    public SaleMan save(SaleMan cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from SaleMan o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<SaleMan> findAll(String compCode) {
        String hsql = "select o from SaleMan o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public SaleMan find(SaleManKey key) {
        return getByKey(key);
    }

}
