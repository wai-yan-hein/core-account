package com.h2.service;

import com.h2.dao.LanguageDao;
import com.inventory.entity.Language;
import com.inventory.entity.LanguageKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageDao dao;
    @Autowired
    private SeqService seqService;

    @Override
    public Language save(Language status) {
        return dao.save(status);
    }

    @Override
    public List<Language> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(LanguageKey key) {
        return dao.delete(key);
    }

    @Override
    public Language findById(LanguageKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Language> search(String description) {
        return dao.search(description);
    }

    @Override
    public List<Language> unUpload() {
        return dao.unUpload();
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Language> getLanguage(LocalDateTime updatedDate) {
        return dao.getLanguage(updatedDate);
    }

}
