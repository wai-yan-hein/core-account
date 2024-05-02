package com.h2.dao;

import com.common.ReportFilter;
import com.inventory.entity.Job;
import com.inventory.entity.JobKey;
import java.util.List;

public interface JobDao {

    Job save(Job Job);

    List<Job> findAll(ReportFilter filter);

    List<Job> getActiveJob(String compCode);

    int delete(JobKey key);

    Job findById(JobKey id);

    List<Job> search(String des);

    List<Job> unUpload();

    String getMaxDate();
}
