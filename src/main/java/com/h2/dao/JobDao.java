package com.h2.dao;

import com.inventory.model.Job;
import com.inventory.model.JobKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface JobDao {

    Job save(Job Job);

    List<Job> findAll(String compCode, Boolean isFinished);

    int delete(JobKey key);

    Job findById(JobKey id);

    List<Job> search(String des);

    List<Job> unUpload();

    String getMaxDate();
}
