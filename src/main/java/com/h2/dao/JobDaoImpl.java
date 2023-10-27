package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Job;
import com.inventory.model.JobKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class JobDaoImpl extends AbstractDao<JobKey, Job> implements JobDao {

    @Override
    public Job save(Job job) {
        job.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(job, job.getKey());
        return job;
    }

    @Override
    public List<Job> findAll(String compCode, Boolean isFinished) {
        String hsql = "select o from Job o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        if (isFinished) {
            hsql += " and o.finished = true";
        }
        return findHSQL(hsql);
    }

    @Override
    public int delete(JobKey key) {
        remove(key);
        return 1;
    }

    @Override
    public Job findById(JobKey id) {
        return getByKey(id);
    }

    @Override
    public List<Job> search(String des) {
        String strSql = "";

        if (!des.equals("-")) {
            strSql = "o.name like '%" + des + "%'";
        }

        if (strSql.isEmpty()) {
            strSql = "select o from Job o";
        } else {
            strSql = "select o from Job o where " + strSql;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<Job> unUpload() {
        String hsql = "select o from Job o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Job o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
}
