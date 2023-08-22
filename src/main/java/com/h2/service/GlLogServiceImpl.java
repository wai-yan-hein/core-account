package com.h2.service;
import com.acc.model.GlLog;
import com.h2.dao.GlLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Lazy
@Service
@Transactional
public class GlLogServiceImpl implements GlLogService {
    @Autowired
    private GlLogDao dao;

    @Override
    public GlLog save(GlLog log) {
        return dao.save(log);
    }
}
