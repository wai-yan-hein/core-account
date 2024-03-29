/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.StockBrand;
import com.inventory.entity.StockBrandKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class BrandDaoImpl extends AbstractDao<StockBrandKey, StockBrand> implements BrandDao {

    @Override
    public StockBrand save(StockBrand cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from StockBrand o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<StockBrand> findAll(String compCode) {
        String hsql = "select o from StockBrand o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public StockBrand find(StockBrandKey key) {
        return getByKey(key);
    }

}
