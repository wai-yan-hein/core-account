/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.h2.dao.PurHisDetailDao;
import com.inventory.model.PurDetailKey;
import com.inventory.model.PurHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.annotation.Lazy;

/**
 * @author wai yan
 */
@Slf4j
@Lazy
@Service
@Transactional
public class PurHisDetailServiceImpl implements PurHisDetailService {

    @Autowired
    private PurHisDetailDao dao;

    @Override
    public PurHisDetail save(PurHisDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<PurHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(PurDetailKey key) {
        return dao.delete(key);
    }
}
