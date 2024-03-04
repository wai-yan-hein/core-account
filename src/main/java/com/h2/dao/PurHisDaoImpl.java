/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.entity.PurHis;
import com.inventory.entity.PurHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class PurHisDaoImpl extends AbstractDao<PurHisKey, PurHis> implements PurHisDao {

    @Autowired
    private PurHisDetailDao dao;

    @Override
    public PurHis save(PurHis sh) {
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String remark, String userCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.equals("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!cusCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.trader.code = '" + cusCode + "'";
            } else {
                strFilter = strFilter + " and o.trader.code = '" + cusCode + "'";
            }
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        String strSql = "select o from PurHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public PurHis findById(PurHisKey id) {
        return getByKey(id);
    }

    @Override
    public void delete(PurHisKey key) {
 
    }

    @Override
    public void restore(PurHisKey key) throws Exception {
  
    }

    @Override
    public List<PurHis> unUploadVoucher(String compCode) {
        String hsql = "select o from PurHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<PurHis> list = findHSQL(hsql);
        list.forEach((s) -> {
            s.setListPD(dao.search(s.getKey().getVouNo(),
                    s.getKey().getCompCode(), s.getDeptId()));
        });
        return list;
    }

    @Override
    public List<PurHis> search(String updatedDate, List<String> keys) {
        List<PurHis> list = new ArrayList<>();
        if (keys != null) {
            for (String locCode : keys) {
                String hql = "select o from PurHis o where o.locCode='" + locCode + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListPD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public PurHis updateACK(PurHisKey key) {
        PurHis ph = getByKey(key);
        ph.setIntgUpdStatus("ACK");
        saveOrUpdate(ph, key);
        return ph;
    }
}
