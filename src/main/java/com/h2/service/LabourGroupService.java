package com.h2.service;

import com.inventory.model.LabourGroup;
import com.inventory.model.LabourGroupKey;

import java.util.List;

public interface LabourGroupService {

    LabourGroup save(LabourGroup status);

    List<LabourGroup> findAll(String compCode);

    int delete(LabourGroupKey key);

    LabourGroup findById(LabourGroupKey key);

    String getMaxDate();

}
