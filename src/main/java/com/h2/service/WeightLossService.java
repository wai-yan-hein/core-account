package com.h2.service;

import com.inventory.entity.WeightLossHis;
import com.inventory.entity.WeightLossHisKey;
import java.util.List;

public interface WeightLossService {

    WeightLossHis save(WeightLossHis l);

    WeightLossHis findById(WeightLossHisKey key);

    void delete(WeightLossHisKey key);

    void restore(WeightLossHisKey key);

    List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId);

    WeightLossHis updateACK(WeightLossHisKey key);

    List<WeightLossHis> unUpload(String compCode);
}
