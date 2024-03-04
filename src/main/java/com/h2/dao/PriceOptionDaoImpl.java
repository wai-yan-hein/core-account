/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.PriceOption;
import com.inventory.entity.PriceOptionKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class PriceOptionDaoImpl extends AbstractDao<PriceOptionKey, PriceOption> implements PriceOptionDao {

    @Override
    public PriceOption save(PriceOption cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from PriceOption o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<PriceOption> findAll(String compCode) {
        String hsql = "select o from PriceOption o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public PriceOption find(PriceOptionKey key) {
        return getByKey(key);
    }

}
