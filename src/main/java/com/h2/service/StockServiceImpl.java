/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.StockDao;
import com.inventory.entity.Stock;
import com.inventory.entity.StockKey;
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
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Stock> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Stock> getStock(String str, String compCode, Integer deptId, boolean contain) {
        return dao.getStock(str, compCode, deptId, contain);
    }

    @Override
    public Stock find(StockKey key) {
        return dao.find(key);
    }

    @Override
    public List<Stock> search(String stockCode, String stockType, String cat, String brand,
            String compCode, Integer deptId, boolean active, boolean deleted) {
        return dao.search(stockCode, stockType, cat, brand, compCode, deptId, active, deleted);
    }

    @Override
    public List<Stock> findActiveStock(String compCode) {
        return dao.findActiveStock(compCode);
    }

    @Override
    public Stock findStockByBarcode(StockKey key) {
        return dao.findStockByBarcode(key);
    }

    @Override
    public Boolean updateDeleted(StockKey key, boolean status) {
        return dao.updateDeleted(key, status);

    }

}
