/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.Currency;
import java.util.Date;
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

}