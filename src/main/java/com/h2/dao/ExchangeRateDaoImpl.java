/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.ExchangeKey;
import com.user.model.ExchangeRate;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class ExchangeRateDaoImpl extends AbstractDao<ExchangeKey, ExchangeRate> implements ExchangeRateDao{

    @Override
    public ExchangeRate save(ExchangeRate exRate) {
        saveOrUpdate(exRate,exRate.getKey());
        return exRate;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from ExchangeRate o";
        Date date = getDate(sql);
        return date == null? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
    
}