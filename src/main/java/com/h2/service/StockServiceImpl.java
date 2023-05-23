/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.StockDao;
import com.inventory.model.Stock;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockServiceImpl implements StockService {

    @Autowired
    private StockDao dao;

    @Override
    public Stock save(Stock stock) {
        return dao.save(stock);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Stock> findAll() {
        return dao.findAll();
    }

}
