package com.h2.service;

import com.common.FilterObject;
import com.inventory.model.ProcessHis;
import com.inventory.model.ProcessHisKey;
import java.util.List;

public interface ProcessHisService {

    ProcessHis save(ProcessHis ph);

    ProcessHis findById(ProcessHisKey key);

    List<ProcessHis> search(String fromDate, String toDate, String vouNo, String processNo,
            String remark, String stockCode, String pt,
            String locCode, boolean finish, boolean deleted, String compCode, Integer deptId);

    void delete(ProcessHisKey key);

    void restore(ProcessHisKey key);

    List<ProcessHis> unUpload(String compCode);

    ProcessHis updateACK(ProcessHisKey key);

    public List<ProcessHis> getProcess(FilterObject filter);
}
