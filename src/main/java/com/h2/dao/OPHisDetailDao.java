package com.h2.dao;

import com.inventory.model.OPHisDetail;
import com.inventory.model.OPHisDetailKey;
import java.util.List;

public interface OPHisDetailDao {

    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(OPHisDetailKey key);

}
