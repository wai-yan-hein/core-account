/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.CurrencyDao;
import com.user.model.Currency;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Lazy
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao curDao;

    @Override
    public Currency save(Currency cur) {
        return curDao.save(cur);
    }

    @Override
    public String getMaxDate() {
        return curDao.getMaxDate();
    }

    @Override
    public List<Currency> findAll() {
        return curDao.findAll();
    }

    @Override
    public Currency findById(String curCode) {
        return curDao.findById(curCode);
    }

}
