/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Trader;
import com.inventory.model.TraderKey;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class TraderInvDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderInvDao {

    @Override
    public Trader save(Trader cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Trader o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Trader> findAll(String compCode) {
        String hsql = "select o from Trader o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public Trader find(TraderKey key) {
        return getByKey(key);
    }

}
