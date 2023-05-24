/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Stock;
import com.inventory.model.StockKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class StockDaoImpl extends AbstractDao<StockKey, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public List<Stock> findAll() {
        String hsql = "select o from Stock o";
        return findHSQL(hsql);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        String hsql = "select o from Stock o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Stock o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
