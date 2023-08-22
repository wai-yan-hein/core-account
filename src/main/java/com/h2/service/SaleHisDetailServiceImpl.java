/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.SaleHisDetailDao;
import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHisDetail;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Lazy
@Service
@Transactional
public class SaleHisDetailServiceImpl implements SaleHisDetailService {

    @Autowired
    private SaleHisDetailDao dao;

    @Override
    public SaleHisDetail save(SaleHisDetail obj) {
        return dao.save(obj);
    }

    @Override
    public SaleHisDetail find(SaleDetailKey key) {
        return dao.find(key);
    }

    @Override
    public void delete(SaleDetailKey key) {
        dao.delete(key);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public List<SaleHisDetail> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public List<SaleHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        return dao.searchDetail(vouNo, compCode, deptId);
    }

}
