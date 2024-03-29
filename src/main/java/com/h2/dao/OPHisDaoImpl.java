package com.h2.dao;

import com.inventory.entity.LocationKey;
import com.inventory.entity.OPHis;
import com.inventory.entity.OPHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class OPHisDaoImpl extends AbstractDao<OPHisKey, OPHis> implements OPHisDao {

    @Autowired
    private OPHisDetailDao dao;

    @Override
    public OPHis save(OPHis op) {
        saveOrUpdate(op, op.getKey());
        return op;
    }

    @Override
    public List<OPHis> search(String compCode) {
        String hsql = "select o from OPHis o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public OPHis findByCode(OPHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<OPHis> unUpload() {
        String hsql = "select o from OPHis o where o.intgUpdStatus is null";
        List<OPHis> list = findHSQL(hsql);
        list.forEach(o -> {
            String compCode = o.getKey().getCompCode();
            String vouNo = o.getKey().getVouNo();
            Integer deptId = o.getDeptId();
            o.setDetailList(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void delete(OPHisKey key) {

    }

    @Override
    public List<OPHis> search(String updatedDate, List<LocationKey> keys) {
        List<OPHis> list = new ArrayList<>();
        if (keys != null) {
            for (LocationKey key : keys) {
                String hql = "select o from OPHis o where o.locCode='" + key.getLocCode() + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setDetailList(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

}
