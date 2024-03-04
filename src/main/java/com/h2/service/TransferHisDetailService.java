package com.h2.service;

import com.inventory.entity.THDetailKey;
import com.inventory.entity.TransferHisDetail;
import java.util.List;

public interface TransferHisDetailService {

    TransferHisDetail save(TransferHisDetail th);

    int delete(THDetailKey key);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);

    List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);
}
