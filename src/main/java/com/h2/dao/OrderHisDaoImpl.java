/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.General;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisKey;
import com.inventory.model.VOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class OrderHisDaoImpl extends AbstractDao<OrderHisKey, OrderHis> implements OrderHisDao {

    @Autowired
    private OrderHisDetailDao dao;

    @Override
    public OrderHis save(OrderHis sh) {
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<OrderHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from OrderHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public OrderHis findById(OrderHisKey id) {
        return getByKey(id);
    }

    @Override
    public void delete(OrderHisKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update order_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);
    }

    @Override
    public void restore(OrderHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update order_his set deleted =0,intg_upd_status=null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);
    }

    @Override
    public List<OrderHis> unUploadVoucher(String compCode) {
        String hsql = "select o from OrderHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<OrderHis> list = findHSQL(hsql);
        list.forEach((s) -> {
            s.setListSH(dao.search(s.getKey().getVouNo(),
                    s.getKey().getCompCode(), s.getKey().getDeptId()));
        });
        return list;
    }

    @Override
    public List<OrderHis> search(String updatedDate, List<String> location) {
        List<OrderHis> list = new ArrayList<>();
        if (location != null) {
            for (String locCode : location) {
                String sql = "select * from order_his o where o.loc_code='" + locCode + "' and o.updated_date > '" + updatedDate + "'";
                ResultSet rs = getResult(sql);
                if (rs != null) {
                    try {
                        while (rs.next()) {
                            //vou_no, trader_code, saleman_code, vou_date, credit_term, cur_code, remark,
                            // vou_total, grand_total, discount, disc_p, tax_amt, tax_p, created_date,
                            // created_by, deleted, paid, vou_balance, updated_by,
                            // updated_date, comp_code, address, order_code, reg_code, loc_code, mac_id,
                            // session_id, intg_upd_status, reference, dept_id
                            OrderHis sh = new OrderHis();
                            OrderHisKey k = new OrderHisKey();
                            k.setVouNo(rs.getString("vou_no"));
                            k.setCompCode(rs.getString("comp_code"));
                            k.setDeptId(rs.getInt("dept_id"));
                            sh.setKey(k);
                            sh.setTraderCode(rs.getString("trader_code"));
                            sh.setSaleManCode(rs.getString("saleman_code"));
                            sh.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                            sh.setCreditTerm(rs.getTimestamp("credit_term").toLocalDateTime());
                            sh.setCurCode(rs.getString("cur_code"));
                            sh.setRemark(rs.getString("remark"));
                            sh.setVouTotal(rs.getFloat("vou_total"));
                            sh.setGrandTotal(rs.getFloat("grand_total"));
                            sh.setDiscount(rs.getFloat("discount"));
                            sh.setDiscP(rs.getFloat("disc_p"));
                            sh.setTaxAmt(rs.getFloat("tax_amt"));
                            sh.setTaxPercent(rs.getFloat("tax_p"));
                            sh.setCreatedDate(rs.getTimestamp("created_date"));
                            sh.setCreatedBy(rs.getString("created_by"));
                            sh.setDeleted(rs.getBoolean("deleted"));
                            sh.setPaid(rs.getFloat("paid"));
                            sh.setBalance(rs.getFloat("vou_balance"));
                            sh.setUpdatedBy(rs.getString("updated_by"));
                            sh.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                            sh.setAddress(rs.getString("address"));
                            sh.setLocCode(rs.getString("loc_code"));
                            sh.setMacId(rs.getInt("mac_id"));
                            sh.setIntgUpdStatus(rs.getString("intg_upd_status"));
                            sh.setReference(rs.getString("reference"));
                            list.add(sh);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }

            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListSH(dao.searchDetail(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(OrderHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql1 = "delete from order_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql2 = "delete from order_his_detail where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        execSql(sql1, sql2);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        General g = new General();
        String sql = "select count(*) vou_count,sum(paid) paid\n"
                + "from order_his\n"
                + "where deleted =0\n"
                + "and date(vou_date)='" + vouDate + "'\n"
                + "and comp_code='" + compCode + "'\n"
                + "and dept_id ='" + depId + "'";
        try {
            ResultSet rs = getResult(sql);
            if (rs.next()) {
                g.setQty(rs.getFloat("vou_count"));
                g.setAmount(rs.getFloat("paid"));
            }
        } catch (Exception e) {
            log.error("getVoucherCount : " + e.getMessage());
        }
        return g;
    }

    @Override
    public OrderHis updateACK(OrderHisKey key) {
        OrderHis oh = getByKey(key);
        oh.setIntgUpdStatus("ACK");
        saveOrUpdate(oh, key);
        return oh;
    }

    @Override
    public List<OrderHis> findAll(String compCode) {
        String hsql = "select o from OrderHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<VOrder> getOrderHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted, String nullBatch, String batchNo, String projectNo, String curCode) {
        List<VOrder> saleList = new ArrayList<>();
        String filter = "";
        String sql = """
                     select a.*,t.trader_name,t.user_code
                                          from (select * 
                                          from order_his
                     """
                + "\n where comp_code='" + compCode + "' and dept_id = " + deptId
                + "\n and CAST(vou_date AS DATE) between  '" + fromDate + "' and '" + toDate + "'"
                + "\n and deleted = " + Boolean.parseBoolean(deleted)
                + "\n and intg_upd_status is null)a join trader t on a.trader_code = t.code";
        try {
            ResultSet rs = getResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VOrder s = new VOrder();
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
            log.error("getOrderHistory : " + e.getMessage());
        }
        return saleList;
    }

}
