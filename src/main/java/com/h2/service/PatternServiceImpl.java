package com.h2.service;

import com.h2.dao.PatternDao;
import com.inventory.model.Pattern;
import com.inventory.model.PatternKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatternServiceImpl implements PatternService {

    @Autowired
    private PatternDao dao;

    @Override
    public Pattern findByCode(PatternKey key) {
        return dao.findByCode(key);
    }

    @Override
    public Pattern save(Pattern p) {

        return dao.save(p);
    }

    @Override
    public List<Pattern> search(String stockCode, String compCode) {
        return dao.search(stockCode, compCode);
    }

    @Override
    public void delete(Pattern pattern) {
        dao.delete(pattern);
    }

    @Override
    public List<Pattern> unUpload() {
        return dao.unUpload();
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }
}
