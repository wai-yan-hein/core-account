/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.BrandDao;
import com.inventory.entity.StockBrand;
import com.inventory.entity.StockBrandKey;
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
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao dao;

    @Override
    public StockBrand save(StockBrand cat) {
        return dao.save(cat);
    }

    @Override
    public List<StockBrand> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public StockBrand find(StockBrandKey key) {
        return dao.find(key);
    }

}
