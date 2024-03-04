/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.h2.dao.RetInDetailDao;
import com.inventory.entity.RetInHisDetail;
import com.inventory.entity.RetInKey;
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
public class RetInDetailServiceImpl implements RetInDetailService {

    @Autowired
    private RetInDetailDao dao;

    @Override
    public RetInHisDetail save(RetInHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<RetInHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(RetInKey key) {
        return dao.delete(key);
    }

}
