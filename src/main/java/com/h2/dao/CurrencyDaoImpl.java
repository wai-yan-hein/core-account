/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.Currency;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class CurrencyDaoImpl extends AbstractDao<String, Currency> implements CurrencyDao {
    
    @Override
    public Currency save(Currency cur) {
        saveOrUpdate(cur, cur.getCurCode());
        return cur;
    }
    
    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Currency o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
    
    @Override
    public List<Currency> findAll() {
        String sql = "select o from Currency o";
        return findHSQL(sql);
    }
    
    @Override
    public Currency findById(String curCode) {
        String sql = "select o from Currency o where o.curCode = '" + curCode + "'";
        List<Currency> list = findHSQL(sql);
        return list.isEmpty() ? null : list.get(0);
    }
    
}
