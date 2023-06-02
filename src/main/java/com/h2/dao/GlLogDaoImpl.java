package com.h2.dao;
import com.acc.model.GlLog;
import com.acc.model.GlLogKey;
import org.springframework.stereotype.Repository;

@Repository
public class GlLogDaoImpl extends AbstractDao<GlLogKey,GlLog> implements GlLogDao{
    @Override
    public GlLog save(GlLog log) {
        saveOrUpdate(log,log.getKey());
        return log;
    }
}
