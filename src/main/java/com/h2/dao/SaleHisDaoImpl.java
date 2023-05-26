/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisKey;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class SaleHisDaoImpl extends AbstractDao<SaleHisKey, SaleHis> implements SaleHisDao {

    @Override
    public SaleHis save(SaleHis cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from SaleHis o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<SaleHis> findAll(String compCode) {
        String hsql = "select o from SaleHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public SaleHis find(SaleHisKey key) {
        return getByKey(key);
    }

}
