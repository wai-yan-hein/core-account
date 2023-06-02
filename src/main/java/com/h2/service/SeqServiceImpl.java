/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.SeqDao;
import com.inventory.model.SeqKey;
import com.inventory.model.SeqTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class SeqServiceImpl implements SeqService {
    
    @Override
    public SeqTable save(SeqTable st){
        st = dao.save(st);
        return st;
    }
    
    @Override
    public SeqTable findById(SeqKey id){
        return dao.findById(id);
    }

    @Autowired
    private SeqDao dao;
    
    @Override
    public int getSequence(Integer macId,String option, String period, String compCode){
        return dao.getSequence(macId,option, period, compCode);
    }

}
