/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.StockBrand;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface BrandDao {

    StockBrand save(StockBrand stock);

    String getMaxDate();

    List<StockBrand> findAll(String compCode);

}
