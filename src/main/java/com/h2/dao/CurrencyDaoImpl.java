/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.user.model.Currency;
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

}
