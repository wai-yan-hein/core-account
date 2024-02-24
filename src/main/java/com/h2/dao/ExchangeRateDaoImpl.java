/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.ExchangeKey;
import com.user.model.ExchangeRate;
import java.time.LocalDateTime;
import java.util.List;
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
        LocalDateTime date = getDate(sql);
        return date == null? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<ExchangeRate> searchExchange(String startDate, String endDate, String targetCur, String compCode) {
        String sql = "select o from ExchangeRate o where o.exDate between '" + startDate + "' and '" + endDate + "'\n";
        String filter = "";
        if (!targetCur.equals("-")) {
            filter += "and o.targetCur = '" + targetCur + "'";
        }
        if (!compCode.equals("-")) {
           filter += "and o.key.compCode = '" + compCode + "'";
        }

        return findHSQL(sql + filter);
    }
    
}
