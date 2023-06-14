/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisKey;
import com.inventory.model.VSale;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class SaleHisDaoImpl extends AbstractDao<SaleHisKey, SaleHis> implements SaleHisDao {

    @Autowired
    SaleHisDetailDao dao;

    @Override
    public SaleHis save(SaleHis cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from SaleHis o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<SaleHis> findAll(String compCode) {
        String hsql = "select o from SaleHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public SaleHis find(SaleHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<SaleHis> unUploadVoucher(String compCode) {
        String hsql = "select o from SaleHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<SaleHis> list = findHSQL(hsql);
        list.forEach((s) -> {
            s.setListSH(dao.search(s.getKey().getVouNo(),
                    s.getKey().getCompCode(), s.getKey().getDeptId()));
        });
        return list;
    }

    @Override
    public SaleHis updateACK(SaleHisKey key) {
        SaleHis sh = getByKey(key);
        sh.setIntgUpdStatus("ACK");
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
            String remark, String reference, String userCode, String stockCode,
            String locCode, String compCode, Integer deptId, String deleted,
            String nullBatch, String batchNo, String projectNo, String curCode) {
        List<VSale> saleList = new ArrayList<>();
        String filter = "";
        String sql = """
                     select a.*,t.trader_name,t.user_code
                                          from (select * 
                                          from sale_his
                     """
                + "\n where comp_code='" + compCode + "' and dept_id = " + deptId 
                + "\n and CAST(vou_date AS DATE) between  '" + fromDate + "' and '" + toDate + "'"
                + "\n and deleted = " + Boolean.parseBoolean(deleted)
                + "\n and intg_upd_status is null)a join trader t on a.trader_code = t.code";
        try {
            ResultSet rs = getResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VSale s = new VSale();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setTraderCode(rs.getString("user_code"));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setReference(rs.getString("reference"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setPaid(rs.getFloat("paid"));
                    s.setVouTotal(rs.getFloat("vou_total"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setDeptId(rs.getInt("dept_id"));
                    saleList.add(s);
                }
            }
        } catch (SQLException e) {
            log.error("getSaleHistory : " + e.getMessage());
        }
        return saleList;
    }

}
