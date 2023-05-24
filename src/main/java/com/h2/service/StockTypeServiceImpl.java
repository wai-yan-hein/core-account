/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.StockTypeDao;
import com.inventory.model.StockType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockTypeServiceImpl implements StockTypeService {

    @Autowired
    private StockTypeDao dao;

    @Override
    public StockType save(StockType type) {
        return dao.save(type);
    }

    @Override
    public List<StockType> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaDate() {
        return dao.getMaDate();
    }

}