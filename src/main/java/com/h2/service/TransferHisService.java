package com.h2.service;

import com.common.ReportFilter;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.VTransfer;
import java.util.List;

public interface TransferHisService {

    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload(String syncDate);

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);


    List<TransferHis> search(String updatedDate, List<String> keys);

    void truncate(TransferHisKey key);

    TransferHis updateACK(TransferHisKey key);
    
    public List<VTransfer> getTransfer(ReportFilter filter);
}
