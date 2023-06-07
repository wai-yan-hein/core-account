package com.h2.dao;

import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import java.util.List;

public interface ProcessHisDetailDao {

    ProcessHisDetail save(ProcessHisDetail ph);

    ProcessHisDetail findById(ProcessHisDetailKey key);

    List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId);

    void delete(ProcessHisDetailKey key);
}