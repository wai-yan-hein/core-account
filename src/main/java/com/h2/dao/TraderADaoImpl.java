/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.common.Util1;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class TraderADaoImpl extends AbstractDao<TraderAKey, TraderA> implements TraderADao {

    @Override
    public TraderA save(TraderA trader) {
        saveOrUpdate(trader, trader.getKey());
        return trader;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from TraderA o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<TraderA> findAll(String compCode) {
        String hsql = "select o from TraderA o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

}
