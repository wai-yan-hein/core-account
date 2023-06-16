package com.h2.service;

import com.inventory.model.LocationKey;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisKey;
import java.util.Date;
import java.util.List;

public interface OPHisService {

    OPHis save(OPHis op);

    OPHis findByCode(OPHisKey key);

    List<OPHis> search(String compCode);

    List<OPHis> unUpload();

    void delete(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);

}
