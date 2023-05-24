/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.StockType;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockTypeDao {

    StockType save(StockType type);

    List<StockType> findAll(String compCode);

    String getMaDate();
}
