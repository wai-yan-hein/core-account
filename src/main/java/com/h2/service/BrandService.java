/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.StockBrand;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface BrandService {

    StockBrand save(StockBrand stock);

    List<StockBrand> findAll(String compCode);

    String getMaxDate();

}
