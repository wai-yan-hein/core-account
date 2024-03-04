package com.h2.dao;

import com.inventory.entity.OPHisDetail;
import com.inventory.entity.OPHisDetailKey;
import java.util.List;

public interface OPHisDetailDao {

    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(OPHisDetailKey key);

}
