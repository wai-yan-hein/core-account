/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.h2.dao.COADao;
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
public class COAServiceImpl implements COAService {

    @Autowired
    private COADao dao;

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        return dao.save(coa);
    }

    @Override
    public List<ChartOfAccount> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public ChartOfAccount findById(COAKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ChartOfAccount> getCOA(String headCode, String compCode) {
        return dao.getCOA(headCode, compCode);
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        return dao.getCOAChild(parentCode, compCode);
    }

    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        return dao.getCOATree(compCode);
    }

    @Override
    public List<ChartOfAccount> getTraderCOA(String compCode) {
        return dao.getTraderCOA(compCode);
    }

    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        return dao.searchCOA(str, level, compCode);
    }
}
