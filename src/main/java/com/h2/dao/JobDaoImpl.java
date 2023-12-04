package com.h2.dao;

import com.common.FilterObject;
import com.common.Util1;
import com.inventory.model.Job;
import com.inventory.model.JobKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<Job> findAll(FilterObject filterObject) {
        String compCode = filterObject.getCompCode();
        Integer deptId = filterObject.getDeptId();
        String fromDate = filterObject.getFromDate();
        String toDate = filterObject.getToDate();
        Boolean finished = filterObject.getFinished();
        List<Job> jList = new ArrayList<>();
        String whereClause = "";
        if (finished != null) {
            whereClause += " and finished = " + finished;
        }
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            whereClause += " and start_date between '" + fromDate + "' and '" + toDate + "'"
                    + "and end_date between '" + fromDate + "' and '" + toDate + "'";
        }
        String sql = """ 
                select * from Job
                where deleted = false
                and dept_id = ?
                and comp_code = ?
                """ + whereClause;
        ResultSet rs = getResult(sql, deptId, compCode);

        if (rs != null) {
            try {
                while (rs.next()) {
                    Job job = new Job();
                    JobKey jKey = new JobKey();
                    jKey.setJobNo(rs.getString("job_no"));
                    jKey.setCompCode(rs.getString("comp_code"));
                    job.setKey(jKey);
                    job.setJobName(rs.getString("job_name"));
                    job.setStartDate(rs.getDate("start_date"));
                    job.setEndDate(rs.getDate("end_date"));
                    job.setDeptId(rs.getInt("dept_id"));
                    job.setCreatedBy(rs.getString("created_by"));
                    jList.add(job);
                }
            } catch (SQLException e) {
                log.info(e.getMessage());
            }
        }
        return jList;
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
