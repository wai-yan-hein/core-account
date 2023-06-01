package com.h2.service;

import com.h2.dao.ProcessHisDetailDao;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessHisDetailServiceImpl implements ProcessHisDetailService {

    @Autowired
    private ProcessHisDetailDao dao;

    @Override
    public ProcessHisDetail save(ProcessHisDetail ph) {
        return dao.save(ph);
    }

    @Override
    public ProcessHisDetail findById(ProcessHisDetailKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public void delete(ProcessHisDetailKey key) {
        dao.delete(key);
    }
}
