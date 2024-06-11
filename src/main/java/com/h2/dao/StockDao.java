/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.Stock;
import com.inventory.entity.StockKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockDao {

    Stock save(Stock stock);

    Stock find(StockKey key);

    Boolean updateDeleted(StockKey key,boolean status);

    Stock findStockByBarcode(StockKey key);

    String getMaxDate();

    List<Stock> getStock(String str, String compCode, Integer deptId,boolean contain);

    List<Stock> findAll(String compCode);

    List<Stock> findAll();

    List<Stock> findActiveStock(String compCode);

    List<Stock> search(String stockCode, String stockType, String cat,
            String brand, String compCode, Integer deptId, boolean active, boolean deleted);

}
