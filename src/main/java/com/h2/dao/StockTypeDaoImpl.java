/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.StockType;
import com.inventory.entity.StockTypeKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockTypeDaoImpl extends AbstractDao<StockTypeKey, StockType> implements StockTypeDao {

    @Override
    public StockType save(StockType type) {
        saveOrUpdate(type, type.getKey());
        return type;
    }

    @Override
    public List<StockType> findAll(String compCode) {
        String jpql = "select o from StockType o where o.key.compCode ='" + compCode + "'";
        return findHSQL(jpql);
    }

    @Override
    public String getMaDate() {
        String jpql = "select max(o.updatedDate) from StockType o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public StockType find(StockTypeKey type) {
        return getByKey(type);
    }

}
