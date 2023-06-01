package com.h2.service;

import com.h2.dao.TransferHisDetailDao;
import com.inventory.model.THDetailKey;
import com.inventory.model.TransferHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
