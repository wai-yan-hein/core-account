package com.h2.dao;


import com.inventory.model.LabourGroup;
import com.inventory.model.LabourGroupKey;

import java.time.LocalDateTime;
import java.util.List;

public interface LabourGroupDao {
    LabourGroup save(LabourGroup LabourGroup);

    List<LabourGroup> findAll(String compCode);

    int delete(LabourGroupKey key);

    LabourGroup findById(LabourGroupKey id);


    String getMaxDate();

}
