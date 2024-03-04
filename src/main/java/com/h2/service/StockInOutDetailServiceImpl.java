/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.h2.dao.StockInOutDetailDao;
import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.StockInOutKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.annotation.Lazy;

/**
 * @author wai yan
 */
@Lazy
@Service
@Transactional
public class StockInOutDetailServiceImpl implements StockInOutDetailService {

    @Autowired
    private StockInOutDetailDao dao;

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        return dao.save(stock);
    }

    @Override
    public int delete(StockInOutKey key) {
        return dao.delete(key);
    }

    @Override
    public List<StockInOutDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

}
