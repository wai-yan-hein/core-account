package com.h2.service;


import com.h2.dao.LabourGroupDao;
import com.inventory.entity.LabourGroup;
import com.inventory.entity.LabourGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LabourGroupServiceImpl implements LabourGroupService{

    @Autowired
    private LabourGroupDao dao;
    @Autowired
    private SeqService seqService;
    @Override
    public LabourGroup save(LabourGroup status) {
        if (Objects.isNull(status.getKey().getCode())) {
            String compCode = status.getKey().getCompCode();
            status.getKey().setCode(getCode(compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<LabourGroup> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(LabourGroupKey key) {
        return dao.delete(key);
    }

    @Override
    public LabourGroup findById(LabourGroupKey key) {
        return dao.findById(key);
    }
    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "LabourGroup", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }
}
