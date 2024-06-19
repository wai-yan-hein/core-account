package com.h2.dao;

import com.common.ReportFilter;
import com.common.Util1;
import com.inventory.entity.Job;
import com.inventory.entity.JobKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Service
@Transactional
@Slf4j
public class JobRepo extends AbstractDao<JobKey, Job> {

    public Job save(Job job) {
        job.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(job, job.getKey());
        return job;
    }

    public List<Job> findAll(ReportFilter ReportFilter) {
        String compCode = ReportFilter.getCompCode();
        Integer deptId = ReportFilter.getDeptId();
        String fromDate = ReportFilter.getFromDate();
        String toDate = ReportFilter.getToDate();
        Boolean finished = ReportFilter.isFinished();
        List<Job> jList = new ArrayList<>();
        String whereClause = "";
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            whereClause += " and start_date >='" + fromDate + "'\n"
                    + "and end_date <='" + toDate + "'";
        }
        String sql = """ 
                select * 
                from job
                where deleted = false
                and dept_id = ?
                and comp_code = ?
                and finished =?
                """ + whereClause;
        ResultSet rs = getResult(sql, deptId, compCode, finished);

        if (rs != null) {
            try {
                while (rs.next()) {
                    Job job = new Job();
                    JobKey jKey = new JobKey();
                    jKey.setJobNo(rs.getString("job_no"));
                    jKey.setCompCode(rs.getString("comp_code"));
                    job.setKey(jKey);
                    job.setJobName(rs.getString("job_name"));
                    job.setStartDate(rs.getDate("start_date").toLocalDate());
                    job.setEndDate(rs.getDate("end_date").toLocalDate());
                    job.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                    job.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    job.setDeptId(rs.getInt("dept_id"));
                    job.setCreatedBy(rs.getString("created_by"));
                    job.setUpdatedBy(rs.getString("updated_by"));
                    job.setDeleted(rs.getBoolean("deleted"));
                    job.setFinished(rs.getBoolean("finished"));
                    job.setOutputCost(rs.getDouble("output_cost"));
                    job.setOutputQty(rs.getDouble("output_qty"));
                    jList.add(job);
                }
            } catch (SQLException e) {
                log.info(e.getMessage());
            }
        }
        return jList;
    }

    public int delete(JobKey key) {
        remove(key);
        return 1;
    }

    public Job findById(JobKey id) {
        return getByKey(id);
    }

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

    public List<Job> unUpload() {
        String hsql = "select o from Job o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Job o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    public List<Job> getActiveJob(String compCode) {
        String sql = "select o from Job o where o.key.compCode = '" + compCode + "' and o.deleted = false";
        return findHSQL(sql);
    }
}
