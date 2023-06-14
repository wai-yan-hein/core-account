/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.Util1;
import com.h2.dao.ReportDao;
import com.inventory.model.General;
import com.inventory.model.VPurchase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Slf4j
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao reportDao;

    @Override
    public General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" + "from (\n"
                + "select pur_unit,pur_price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n"
                + "from v_purchase pd\n" + "join v_relation rel on pd.rel_code = rel.rel_code\n"
                + "and pd.pur_unit =  rel.unit\n" + "where pd.stock_code = '" + stockCode
                + "' and vou_no = (\n" + "select ph.vou_no\n" + "from pur_his ph, pur_his_detail pd\n"
                + "where date(ph.vou_date)<= '" + purDate + "' \n" + "and deleted = FALSE\n" + "and ph.comp_code = '" + compCode
                + "' and ph.vou_no = pd.vou_no\n" + "and ph.dept_id = " + deptId + "\n" + "and pd.stock_code = '" + stockCode
                + "'\n" + "group by ph.vou_no\n" + "order by ph.vou_date desc\n" + "limit 1\n" + "))a\n" + "join v_relation rel\n"
                + "on a.rel_code =rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.dept_id = rel.dept_id\n"
                + "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getFloat("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId) {
        General g = new General();
        g.setSmallQty(1.0f);
        String sql = "select ud.qty,ud.smallest_qty\n" + "from stock s join unit_relation_detail ud\n"
                + "on s.rel_code = ud.rel_code\n" + "and s.comp_code =ud.comp_code\n" + "and s.dept_id =ud.dept_id\n"
                + "where s.stock_code ='" + stockCode + "'\n" + "and s.comp_code ='" + compCode + "'\n"
                + "and s.dept_id =" + deptId + "\n" + "and ud.unit ='" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                g.setQty(rs.getFloat("qty"));
                g.setSmallQty(rs.getFloat("smallest_qty"));
            }
        } catch (Exception e) {
            log.error(String.format("getSmallestQty: %s", e.getMessage()));
        }
        return g;
    }

    //gl
    @Override
    public List<Gl> getIndividualLedger(String fromDate, String toDate, String desp, String srcAcc, String acc,
            String curCode, String reference, String compCode, String tranSource,
            String traderCode, String traderType, String coaLv2, String coaLv1, String batchNo,
            String projectNo, boolean summary, Integer macId) {
        String coaFilter = "";
        List<Gl> list = new ArrayList<>();
        try{
        if (!coaLv2.equals("-")) {
            coaFilter += "where coa3.coa_parent = '" + coaLv2 + "'\n";
        }
        if (!coaLv1.equals("-")) {
            if (coaFilter.isEmpty()) {
                coaFilter += "where coa2.coa_parent = '" + coaLv1 + "'\n";
            } else {
                coaFilter += "and coa2.coa_parent = '" + coaLv1 + "'\n";
            }
        }
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!tranSource.equals("-")) {
            filter += "and tran_source = '" + tranSource + "'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference + "%'\n";
        }
        if (!desp.equals("-")) {
            filter += "and description like '" + desp + "%'\n";
        }
        if (!acc.equals("-")) {
            filter += "and (account_id = '" + acc + "' or source_ac_id ='" + acc + "')";
        }
        if (!traderType.equals("-")) {
            filter += "and  discriminator ='" + traderType + "' \n";
        }
        if (!curCode.equals("-")) {
            filter += "and cur_code ='" + curCode + "'\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no ='" + batchNo + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }
        if (summary) {
            String sql = "select a.*,dep.usr_code d_user_code,coa.coa_name_eng src_acc_name,coa3.coa_name_eng acc_name\n" + "from (\n" + "select gl_date,gl_code,dept_id,cur_code,source_ac_id,account_id,dept_code,trader_code,comp_code,sum(dr_amt) dr_amt,sum(cr_amt) cr_amt\n" + "from gl \n" + "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" + "and deleted =0\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" + filter + "\n" + "group by source_ac_id,account_id,dept_code\n" + ")a\n" + "join department dep\n" + "on a.dept_code = dep.dept_code\n" + "and a.comp_code = dep.comp_code\n" + "join chart_of_account coa\n" + "on a.source_ac_id = coa.coa_code\n" + "and a.comp_code = coa.comp_code\n" + "left join chart_of_account coa3\n" + "on a.account_id = coa3.coa_code\n" + "and a.comp_code = coa3.comp_code\n" + "left join chart_of_account coa2\n" + "on coa3.coa_parent = coa2.coa_code\n" + "and coa3.comp_code = coa2.comp_code\n" + coaFilter + "\n" + "order by coa.coa_code_usr\n";
            ResultSet rs = reportDao.executeAndResult(sql);
            if (!Objects.isNull(rs)) {
                try {
                    while (rs.next()) {
                        Gl v = new Gl();
                        GlKey key = new GlKey();
                        key.setCompCode(compCode);
                        key.setGlCode(rs.getString("gl_code"));
                        key.setDeptId(rs.getInt("dept_id"));
                        v.setKey(key);
                        v.setGlDate(rs.getDate("gl_date"));
                        v.setVouDate(Util1.toDateStr(v.getGlDate(), "dd/MM/yyyy"));
                        v.setCurCode(rs.getString("cur_code"));
                        v.setSrcAccCode(rs.getString("source_ac_id"));
                        v.setAccCode(rs.getString("account_id"));
                        v.setDrAmt(rs.getDouble("dr_amt"));
                        v.setCrAmt(rs.getDouble("cr_amt"));
                        try {
                            v.setDeptUsrCode(rs.getString("d_user_code"));
                        } catch (SQLException ex) {
                            Logger.getLogger(ReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        v.setSrcAccName(rs.getString("src_acc_name"));
                        v.setAccName(rs.getString("acc_name"));
                        v.setTranSource("Report");
                        list.add(v);
                    }
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }
            }
        } else {
            String sql = "select a.*,dep.usr_code d_user_code,t.user_code t_user_code,t.discriminator,t.trader_name,coa.coa_name_eng src_acc_name,coa3.coa_name_eng acc_name\n" + "from (\n" + "select gl_code, gl_date, created_date, description, source_ac_id, account_id, \n" + "cur_code, dr_amt, cr_amt, reference, dept_code, voucher_no, trader_code, comp_code, tran_source, gl_vou_no,\n" + "remark, mac_id, ref_no,dept_id,batch_no,project_no\n" + "from gl \n" + "where date(gl_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" + "and deleted =0\n" + "and dept_code in (select dept_code from tmp_dep_filter where mac_id =" + macId + ")\n" + "and (account_id = '" + srcAcc + "' or source_ac_id ='" + srcAcc + "')\n" + "" + filter + "\n" + "order by gl_date,tran_source,gl_code\n" + ")a\n" + "join department dep\n" + "on a.dept_code = dep.dept_code\n" + "and a.comp_code = dep.comp_code\n" + "left join trader t on \n" + "a.trader_code = t.code\n" + "and a.comp_code = t.comp_code\n" + "join chart_of_account coa\n" + "on a.source_ac_id = coa.coa_code\n" + "and a.comp_code = coa.comp_code\n" + "left join chart_of_account coa3\n" + "on a.account_id = coa3.coa_code\n" + "and a.comp_code = coa3.comp_code\n" + "left join chart_of_account coa2\n" + "on coa3.coa_parent = coa2.coa_code\n" + "and coa3.comp_code = coa2.comp_code\n" + "" + coaFilter + "\n" + "order by a.gl_date,a.tran_source,a.gl_code\n";
            ResultSet rs = reportDao.executeAndResult(sql);
            try {
                while (rs.next()) {
                    Gl v = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setGlCode(rs.getString("gl_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    v.setKey(key);
                    v.setGlDate(rs.getTimestamp("gl_date"));
                    v.setCreatedDate(rs.getTimestamp("created_date"));
                    v.setVouDate(Util1.toDateStr(v.getGlDate(), "dd/MM/yyyy"));
                    v.setDescription(rs.getString("description"));
                    v.setSrcAccCode(rs.getString("source_ac_id"));
                    v.setAccCode(rs.getString("account_id"));
                    v.setCurCode(rs.getString("cur_code"));
                    v.setDrAmt(rs.getDouble("dr_amt"));
                    v.setCrAmt(rs.getDouble("cr_amt"));
                    v.setReference(rs.getString("reference"));
                    v.setRefNo(rs.getString("ref_no"));
                    v.setDeptCode(rs.getString("dept_code"));
                    v.setVouNo(rs.getString("voucher_no"));
                    v.setDeptUsrCode(rs.getString("d_user_code"));
                    v.setTraderCode(rs.getString("trader_code"));
                    v.setTraderName(rs.getString("trader_name"));
                    v.setTranSource(rs.getString("tran_source"));
                    v.setGlVouNo(rs.getString("gl_vou_no"));
                    v.setSrcAccName(rs.getString("src_acc_name"));
                    v.setAccName(rs.getString("acc_name"));
                    v.setMacId(rs.getInt("mac_id"));
                    v.setBatchNo(rs.getString("batch_no"));
                    v.setProjectNo(rs.getString("project_no"));
                    list.add(v);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }

        }
        if (!list.isEmpty()) {
            list.forEach(gl -> {
                String account = Util1.isNull(gl.getAccCode(), "-");
                if (account.equals(srcAcc)) {
                    //swap amt
                    double tmpDrAmt = Util1.getDouble(gl.getDrAmt());
                    gl.setDrAmt(gl.getCrAmt());
                    gl.setCrAmt(tmpDrAmt);
                    //swap acc
                    String tmpStr = gl.getAccName();
                    gl.setAccName(gl.getSrcAccName());
                    gl.setSrcAccName(tmpStr);
                }
                gl.setDrAmt(Util1.toNull(gl.getDrAmt()));
                gl.setCrAmt(Util1.toNull(gl.getCrAmt()));
            });
        }
        }catch(Exception e) {
            e.printStackTrace();
        }
       return list;
    }

    @Override
    public List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo, String userCode, String locCode,
            String compCode, Integer deptId, String deleted) {
        List<VPurchase> purchaseList = new ArrayList<>();
        try {
            String sql = "select a.*,t.trader_name\n"
                    + "from (\n" + "select cast(vou_date as date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id,intg_upd_status\n"
                    + "from pur_his p \n"
                    + "where comp_code = '" + compCode + "'\n"
                    + "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                    + "and deleted =" + deleted + "\n"
                    + "and intg_upd_status is null\n"
                    + "and cast(vou_date as date) between '" + fromDate + "' and '" + toDate + "'\n"
                    + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                    + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n"
                    + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                    + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                    + "group by vou_no)a\n"
                    + "join trader t on a.trader_code = t.code\n"
                    + "and a.comp_code = t.comp_code\n"
                    + "order by cast(vou_date as date),vou_no";
            ResultSet rs = reportDao.executeSql(sql);

            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VPurchase s = new VPurchase();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setPaid(rs.getFloat("paid"));
                    s.setVouTotal(rs.getFloat("vou_total"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setIntgUpdStatus(rs.getString("intg_upd_status"));
                    purchaseList.add(s);
                }
            }
        } catch (SQLException e) {
        }

        return purchaseList;
    }
}
