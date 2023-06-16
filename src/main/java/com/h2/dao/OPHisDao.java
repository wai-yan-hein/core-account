package com.h2.dao;

import com.inventory.model.LocationKey;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisKey;
import java.util.List;

public interface OPHisDao {

    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(OPHisKey key);

    List<OPHis> unUpload();

    void delete(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);


}
