/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.h2.dao.RetOutDetailDao;
import com.inventory.model.RetOutHisDetail;
import com.inventory.model.RetOutKey;
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
public class RetOutDetailServiceImpl implements RetOutDetailService {

    @Autowired
    private RetOutDetailDao dao;

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<RetOutHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(RetOutKey key) {
        return dao.delete(key);
    }

}
