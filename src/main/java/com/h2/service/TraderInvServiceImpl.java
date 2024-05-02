/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.TraderInvDao;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderKey;
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
public class TraderInvServiceImpl implements TraderInvService {

    @Autowired
    private TraderInvDao dao;

    @Override
    public Trader save(Trader cat) {
        return dao.save(cat);
    }

    @Override
    public List<Trader> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public Trader find(TraderKey key) {
        return dao.find(key);
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode) {
        return dao.searchTrader(str, type, compCode);
    }

    @Override
    public List<Trader> getTrader(String compCode, String type) {
        return dao.getTrader(compCode, type);
    }

    @Override
    public Boolean delete(TraderKey key) {
        return dao.delete(key);
    }

}
