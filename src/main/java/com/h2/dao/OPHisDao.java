package com.h2.dao;

import com.inventory.entity.LocationKey;
import com.inventory.entity.OPHis;
import com.inventory.entity.OPHisKey;
import java.util.List;

public interface OPHisDao {

    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(OPHisKey key);

    List<OPHis> unUpload();

    void delete(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);


}
