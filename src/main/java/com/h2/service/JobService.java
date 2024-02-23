package com.h2.service;

import com.common.ReportFilter;
import com.inventory.model.Job;
import com.inventory.model.JobKey;
import java.util.List;

public interface JobService {

    Job save(Job status);

    List<Job> findAll(ReportFilter ReportFilter);

    int delete(JobKey key);

    Job findById(JobKey key);

    List<Job> search(String description);

    List<Job> unUpload();

    String getMaxDate();
}
