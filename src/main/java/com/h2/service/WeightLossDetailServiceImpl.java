package com.h2.service;

import com.h2.dao.WeightLossHisDetailDao;
import com.inventory.model.WeightLossDetail;
import com.inventory.model.WeightLossDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WeightLossDetailServiceImpl implements WeightLossDetailService {
    @Autowired
    private WeightLossHisDetailDao dao;

    @Override
    public WeightLossDetail save(WeightLossDetail wd) {
        return dao.save(wd);
    }

    @Override
    public void delete(WeightLossDetailKey key) {
        dao.delete(key);
    }

    @Override
    public List<WeightLossDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }
}