/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockUnitService {

    StockUnit save(StockUnit stock);

    StockUnit find(StockUnitKey key);

    List<StockUnit> findAll(String compCode);

    String getMaxDate();

}
