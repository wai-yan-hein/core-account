/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;
import com.acc.model.SeqKeyAccount;
import com.acc.model.SeqAccountTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.h2.dao.SeqAccountTableDao;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class SeqTableServiceImpl implements SeqTableService{
    
    @Autowired
    private SeqAccountTableDao dao;
    
    @Override
    public SeqAccountTable save(SeqAccountTable st){
        st = dao.save(st);
        return st;
    }
    
    @Override
    public SeqAccountTable findById(SeqKeyAccount id){
        return dao.findById(id);
    }
    
    @Override
    public List<SeqAccountTable> search(String option, String period, String compCode){
        return dao.search(option, period, compCode);
    }
    
    @Override
    public SeqAccountTable getSeqTable(String option, String period, String compCode){
        return dao.getSeqTable(option, period, compCode);
    }
    
    @Override
    public int delete(Integer id){
        return dao.delete(id);
    }
    
    @Override
    public int getSequence(Integer macId,String option, String period, String compCode){
        return dao.getSequence(macId,option, period, compCode);
    }
    
    @Override
    public List<SeqAccountTable> findAll(String compCode) {
        return dao.findAll(compCode);
    }
}
