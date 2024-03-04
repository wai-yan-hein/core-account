/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.inventory.entity.RetInHis;
import com.inventory.entity.RetInHisKey;
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
public class RetInDaoImpl extends AbstractDao<RetInHisKey, RetInHis> implements RetInDao {

    @Autowired
    private RetInDetailDao dao;

    @Override
    public RetInHis save(RetInHis sh) {
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from RetInHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public RetInHis findById(RetInHisKey id) {
        return getByKey(id);
    }

    @Override
    public void delete(RetInHisKey key) throws Exception {

    }

    @Override
    public void restore(RetInHisKey key) throws Exception {

    }

    @Override
    public List<RetInHis> unUploadVoucher(String compCode) {
        String hsql = "select o from RetInHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<RetInHis> list = findHSQL(hsql);
        list.forEach((s) -> {
            s.setListRD(dao.search(s.getKey().getVouNo(),
                    s.getKey().getCompCode(), s.getDeptId()));
        });
        return list;
    }

    @Override
    public List<RetInHis> search(String updatedDate, List<String> keys) {
        List<RetInHis> list = new ArrayList<>();
        if (keys != null) {
            for (String locCode : keys) {
                String hql = "select o from RetInHis o where o.locCode='" + locCode + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListRD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(RetInHisKey key) {

    }

    @Override
    public RetInHis updateACK(RetInHisKey key) {
        RetInHis rh = getByKey(key);
        rh.setIntgUpdStatus("ACK");
        saveOrUpdate(rh, key);
        return rh;
    }
}
