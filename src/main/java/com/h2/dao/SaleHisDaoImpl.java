/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.VSale;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public SaleHis save(SaleHis sh) {
        saveOrUpdate(sh, sh.getKey());
        sh.setLocal(true);
        return sh;
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
                    s.getKey().getCompCode(), s.getDeptId()));
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
    public void delete(SaleHisKey key) {
        SaleHis sh = getByKey(key);
        sh.setDeleted(true);
        sh.setUpdatedDate(LocalDateTime.now());
        update(sh);
    }

    @Override
    public void restore(SaleHisKey key) {
        SaleHis sh = getByKey(key);
        sh.setDeleted(false);
        sh.setUpdatedDate(LocalDateTime.now());
        update(sh);
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
                     
                      where comp_code='""" + compCode + "' and dept_id = " + deptId
                + "\n and CAST(vou_date AS DATE) between  '" + fromDate + "' and '" + toDate + "'"
                + "\n and deleted = " + Boolean.valueOf(deleted)
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
                    s.setPaid(rs.getDouble("paid"));
                    s.setVouTotal(rs.getDouble("vou_total"));
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

    @Override
    public List<VSale> getSaleReport(String vouNo, String compCode, Integer deptId) {
        String sql = """
                     select sh.vou_no,sh.trader_code,sh.saleman_code,sh.vou_date,sh.cur_code,sh.remark,
                     sh.vou_total,sh.vou_balance,sh.grand_total,sh.discount,sh.disc_p,sh.tax_amt,sh.tax_p,sh.paid,
                     sh.comp_code,shd.weight,shd.weight_unit,
                     shd.stock_code,shd.qty,shd.sale_unit,shd.sale_price,shd.sale_amt,
                     t.user_code t_user_code,t.trader_name,t.phone,t.address,t.rfid,
                     s.user_code s_user_code,s.stock_name,l.loc_name
                     from sale_his sh join sale_his_detail shd
                     on sh.vou_no =shd.vou_no
                     and sh.comp_code = shd.comp_code
                     and sh.dept_id =shd.dept_id
                     join trader t on sh.trader_code = t.code
                     and sh.comp_code = t.comp_code
                     and sh.dept_id = t.dept_id
                     join location l on sh.loc_code = l.loc_code
                     and sh.comp_code = l.comp_code
                     and sh.dept_id = l.dept_id
                     join stock s on shd.stock_code = s.stock_code
                     and shd.comp_code = s.comp_code
                     and shd.dept_id = s.dept_id
                     where sh.vou_no ='""" + vouNo + "'\n"
                + "and sh.comp_code ='" + compCode + "'\n"
                + "and sh.dept_id =" + deptId + "";
        List<VSale> list = new ArrayList<>();
        ResultSet rs = getResult(sql);
        try {
            while (rs.next()) {
                VSale sale = new VSale();
                String remark = rs.getString("remark");
                String refNo = "-";

                if (remark != null) {
                    if (remark.contains("/")) {
                        try {
                            String[] split = remark.split("/");
                            remark = split[0];
                            refNo = split[1];
                        } catch (Exception ignored) {
                        }
                    }
                }
                sale.setTraderCode(rs.getString("t_user_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setRemark(remark);
                sale.setRefNo(refNo);
                sale.setPhoneNo(rs.getString("phone"));
                sale.setAddress(rs.getString("address"));
                sale.setRfId(rs.getString("rfid"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setVouDate(Util1.toDateStr(rs.getTimestamp("vou_date"), "dd/MM/yyyy"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getDouble("qty"));
                sale.setSalePrice(rs.getDouble("sale_price"));
                sale.setSaleAmount(rs.getDouble("sale_amt"));
                sale.setVouTotal(rs.getDouble("vou_total"));
                sale.setDiscount(rs.getDouble("discount"));
                sale.setPaid(rs.getDouble("paid"));
                sale.setVouBalance(rs.getDouble("vou_balance"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setCusAddress(Util1.isNull(rs.getString("phone"), "") + "/" + Util1.isNull(rs.getString("address"), ""));
                sale.setLocationName(rs.getString("loc_name"));
                sale.setCompCode(rs.getString("comp_code"));
                sale.setWeight(rs.getDouble("weight"));
                sale.setWeightUnit(rs.getString("weight_unit"));
                list.add(sale);
            }
        } catch (SQLException e) {
            log.error("getSaleReport : " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getUploadCount() {
        String sql = "select count(*) vou_count from sale_his where intg_upd_status is null";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                return rs.getInt("vou_count");
            }
        } catch (SQLException e) {
            log.error("getUploadCount : " + e.getMessage());
            return 0;
        }
        return 0;
    }

}
