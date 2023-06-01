package com.h2.service;

import com.inventory.model.OPHisDetail;
import java.util.List;

public interface OPHisDetailService {

    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);
}
