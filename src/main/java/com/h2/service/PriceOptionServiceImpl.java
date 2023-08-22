/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.PriceOptionDao;
import com.inventory.model.PriceOption;
import com.inventory.model.PriceOptionKey;
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
public class PriceOptionServiceImpl implements PriceOptionService {

    @Autowired
    private PriceOptionDao dao;

    @Override
    public PriceOption save(PriceOption cat) {
        return dao.save(cat);
    }

    @Override
    public List<PriceOption> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public PriceOption find(PriceOptionKey key) {
        return dao.find(key);
    }

}
