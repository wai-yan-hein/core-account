/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.Stock;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockService {

    Stock save(Stock stock);

    List<Stock> findAll(String compCode);

    List<Stock> getStock(String str, String compCode, Integer deptId);

    String getMaxDate();

    List<Stock> findAll();
}
