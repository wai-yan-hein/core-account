package com.h2.dao;


import com.inventory.entity.LabourGroup;
import com.inventory.entity.LabourGroupKey;

import java.util.List;

public interface LabourGroupDao {
    LabourGroup save(LabourGroup LabourGroup);

    List<LabourGroup> findAll(String compCode);

    int delete(LabourGroupKey key);

    LabourGroup findById(LabourGroupKey id);


    String getMaxDate();

}
