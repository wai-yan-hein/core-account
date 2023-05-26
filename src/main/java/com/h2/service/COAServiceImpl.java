/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;
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

}