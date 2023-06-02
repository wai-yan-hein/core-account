/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.SeqKey;
import com.inventory.model.SeqTable;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class SeqDaoImpl extends AbstractDao<SeqKey, SeqTable> implements SeqDao {

    @Override
    public SeqTable save(SeqTable st) {
        saveOrUpdate(st, st.getKey());
        return st;
    }

    @Override
    public SeqTable findById(SeqKey id) {
        return getByKey(id);
    }

//    @Override
//    public int getSequence(String option, String period, String compCode) {
//        SeqKey key = new SeqKey();
//        key.setCompCode(compCode);
//        key.setPeriod(period);
//        key.setSeqOption(option);
//        SeqTable st = getByKey(key);
//        if (st == null) {
//            st = new SeqTable();
//            st.setKey(key);
//            st.setSeqNo(1);
//        } else {
//            st.setSeqNo(st.getSeqNo() + 1);
//        }
//        saveOrUpdate(st, st.getKey());
//        return st.getSeqNo();
//    }
    @Override
    public int getSequence(Integer macId, String option, String period, String compCode) {
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(macId);
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable st = findById(key);
        if (st == null) {
            st = new SeqTable();
            st.setKey(key);
            st.setSeqNo(1);
        } else {
            st.setSeqNo(st.getSeqNo() + 1);
        }
        saveOrUpdate(st, st.getKey());
        return st.getSeqNo();
    }
}
