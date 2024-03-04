/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.entity.StockType;
import com.inventory.entity.StockTypeKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockTypeService {

    StockType save(StockType type);

    StockType find(StockTypeKey type);

    List<StockType> findAll(String compCode);

    String getMaDate();
}
