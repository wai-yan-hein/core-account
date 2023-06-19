package com.h2.dao;

import com.common.FilterObject;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.VTransfer;
import java.util.List;

public interface TransferHisDao {

    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload(String syncDate);

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);

    List<TransferHis> search(String updatedDate, List<String> location);

    void truncate(TransferHisKey key);

    TransferHis updateACK(TransferHisKey key);

    List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark,
                                       String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted);


}
