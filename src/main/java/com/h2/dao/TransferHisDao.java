package com.h2.dao;

import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
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

}
