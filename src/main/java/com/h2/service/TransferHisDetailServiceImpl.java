package com.h2.service;

import com.h2.dao.TransferHisDetailDao;
import com.inventory.entity.THDetailKey;
import com.inventory.entity.TransferHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.annotation.Lazy;

@Lazy
@Service
@Transactional
public class TransferHisDetailServiceImpl implements TransferHisDetailService {

    @Autowired
    private TransferHisDetailDao dao;

    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        return dao.save(th);
    }

    @Override
    public int delete(THDetailKey key) {
        return dao.delete(key);
    }

    @Override
    public List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        return dao.searchDetail(vouNo, compCode, deptId);
    }
}
