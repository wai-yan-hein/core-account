/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Util1;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class COADaoImpl extends AbstractDao<COAKey, ChartOfAccount> implements COADao {

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        saveOrUpdate(coa, coa.getKey());
        return coa;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.modifiedDate) from ChartOfAccount o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<ChartOfAccount> findAll(String compCode) {
        String hsql = "select o from ChartOfAccount o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
