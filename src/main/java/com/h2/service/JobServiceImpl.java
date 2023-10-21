package com.h2.service;

import com.common.Util1;
import com.h2.dao.JobDao;
import com.inventory.model.Job;
import com.inventory.model.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    JobDao dao;
    @Autowired
    private SeqService seqService;

    @Override
    public Job save(Job status) {
        if (Objects.isNull(status.getKey().getJobNo())) {
            String compCode = status.getKey().getCompCode();
            status.getKey().setJobNo(getCode(compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<Job> findAll(String compCode, Boolean isFinished) {
        return dao.findAll(compCode, isFinished);
    }

    @Override
    public int delete(JobKey key) {
        return dao.delete(key);
    }

    @Override
    public Job findById(JobKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Job> search(String description) {
        return dao.search(description);
    }

    @Override
    public List<Job> unUpload() {
        return dao.unUpload();
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    private String getCode(String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(0, "Job", period, compCode);
        return period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
