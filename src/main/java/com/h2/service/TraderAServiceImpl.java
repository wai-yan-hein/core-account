/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;
import com.acc.model.TraderA;
import com.h2.dao.TraderADao;
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
public class TraderAServiceImpl implements TraderAService {

    @Autowired
    private TraderADao dao;

    @Override
    public TraderA save(TraderA trader) {
        return dao.save(trader);
    }

    @Override
    public List<TraderA> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
    
    @Override
    public List<TraderA> getTrader(String text, String compCode) {
        return dao.getTrader(text,compCode);
    }

}
