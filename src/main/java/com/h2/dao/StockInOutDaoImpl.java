/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.LocationKey;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Repository
public class StockInOutDaoImpl extends AbstractDao<StockIOKey, StockInOut> implements StockInOutDao {

    @Autowired
    private StockInOutDetailDao dao;

    @Override
    public StockInOut findById(StockIOKey id) {
        return getByKey(id);
    }

    @Override
    public StockInOut save(StockInOut stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
            String vouNo, String userCode, String vouStatus) {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.endsWith("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description like '" + desp + "%'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "%'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!vouStatus.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatus.code = '" + vouStatus + "'";
            } else {
                strFilter = strFilter + " and o.vouStatus.code = '" + vouStatus + "'";
            }
        }
        String strSql = "select o from StockInOut o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo desc";
        }
        return findHSQL(strSql);

    }

    @Override
    public void delete(StockIOKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update stock_in_out set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);

    }

    @Override
    public void restore(StockIOKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update stock_in_out set deleted =0 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);
    }

    @Override
    public List<StockInOut> unUpload(String syncDate) {
        String hsql = "select o from StockInOut o where intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        List<StockInOut> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListSH(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_in_out";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getSyncDate();
    }

    @Override
    public List<StockInOut> search(String updatedDate, List<LocationKey> keys) {
        List<StockInOut> list = new ArrayList<>();
        if (keys != null) {
            for (LocationKey key : keys) {
                String hql = "select o from StockInOut o where o.locCode='" + key.getLocCode() + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListSH(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public StockInOut updateACK(StockIOKey key) {
        StockInOut so = getByKey(key);
        so.setIntgUpdStatus("ACK");
        saveOrUpdate(so, key);
        return so;
    }

}
