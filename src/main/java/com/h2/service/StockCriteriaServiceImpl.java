/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.Util1;
import com.h2.dao.StockCriteriaDao;
import com.inventory.model.StockCriteria;
import com.inventory.model.StockCriteriaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Transactional
@Service
@RequiredArgsConstructor
public class StockCriteriaServiceImpl implements StockCriteriaService {

    private final StockCriteriaDao dao;
    private final SeqService seqService;

    @Override
    public StockCriteria save(StockCriteria cat) {
        if (Util1.isNull(cat.getKey().getCriteriaCode())) {
            String compCode = cat.getKey().getCompCode();
            String code = getCode(compCode);
            cat.getKey().setCriteriaCode(code);

        }
        return dao.save(cat);
    }

    @Override
    public List<StockCriteria> findAll(String compCode, boolean active) {
        return dao.findAll(compCode, active);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<StockCriteria> search(String text, String compCode) {
        return dao.search(text, compCode);
    }

    public List<StockCriteria> unUpload() {
        return dao.unUpload();
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public StockCriteria findByCode(StockCriteriaKey key) {
        return dao.findByCode(key);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockCriteria", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }
}
