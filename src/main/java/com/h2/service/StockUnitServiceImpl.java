/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.StockUnitDao;
import com.inventory.model.StockUnit;
import com.inventory.model.StockUnitKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Lazy
@Service
@Transactional
public class StockUnitServiceImpl implements StockUnitService {

    @Autowired
    private StockUnitDao dao;

    @Override
    public StockUnit save(StockUnit stock) {
        return dao.save(stock);
    }

    @Override
    public List<StockUnit> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public StockUnit find(StockUnitKey key) {
        return dao.find(key);
    }

}
