package com.h2.service;

import com.inventory.entity.LocationKey;
import com.inventory.entity.OPHis;
import com.inventory.entity.OPHisKey;
import java.util.List;

public interface OPHisService {

    OPHis save(OPHis op);

    OPHis findByCode(OPHisKey key);

    List<OPHis> search(String compCode);

    List<OPHis> unUpload();

    void delete(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);

}
