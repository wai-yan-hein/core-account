/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.ExchangeRateDao;
import com.user.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class ExchangeRateServiceImpl implements ExchangeRateService{
    @Autowired
    private ExchangeRateDao dao;
    @Override
    public ExchangeRate save(ExchangeRate exRate) {
        return dao.save(exRate);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
    
}
