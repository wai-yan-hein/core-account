package com.h2.service;

import com.inventory.entity.WeightLossDetail;
import com.inventory.entity.WeightLossDetailKey;
import java.util.List;

public interface WeightLossDetailService {

    WeightLossDetail save(WeightLossDetail wd);

    void delete(WeightLossDetailKey key);

    List<WeightLossDetail> search(String vouNo, String compCode, Integer deptId);
}
