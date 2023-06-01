package com.h2.dao;

import com.inventory.model.WeightLossDetail;
import com.inventory.model.WeightLossDetailKey;
import java.util.List;

public interface WeightLossHisDetailDao {

    WeightLossDetail save(WeightLossDetail wd);

    void delete(WeightLossDetailKey key);

    List<WeightLossDetail> search(String vouNo, String compCode, Integer deptId);
}
