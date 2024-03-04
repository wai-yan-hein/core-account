/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.StockBrand;
import com.inventory.entity.StockBrandKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface BrandDao {

    StockBrand save(StockBrand stock);

    StockBrand find(StockBrandKey key);

    String getMaxDate();

    List<StockBrand> findAll(String compCode);

}
