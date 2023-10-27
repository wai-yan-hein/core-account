package com.h2.service;

import com.inventory.model.Job;
import com.inventory.model.JobKey;
import java.util.List;

public interface JobService {

    Job save(Job status);

    List<Job> findAll(String compCode, Boolean isFinished);

    int delete(JobKey key);

    Job findById(JobKey key);

    List<Job> search(String description);

    List<Job> unUpload();

    String getMaxDate();
}
