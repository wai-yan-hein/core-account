/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.StockUnit;
import com.inventory.model.StockUnitKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockUnitDaoImpl extends AbstractDao<StockUnitKey, StockUnit> implements StockUnitDao {

    @Override
    public StockUnit save(StockUnit cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from StockUnit o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<StockUnit> findAll(String compCode) {
        String hsql = "select o from StockUnit o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public StockUnit find(StockUnitKey key) {
        return getByKey(key);
    }

}
