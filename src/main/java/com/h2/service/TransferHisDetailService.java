package com.h2.service;

import com.inventory.model.THDetailKey;
import com.inventory.model.TransferHisDetail;
import java.util.List;

public interface TransferHisDetailService {

    TransferHisDetail save(TransferHisDetail th);

    int delete(THDetailKey key);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);

    List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);
}
