/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.ReportDao;
import com.inventory.model.General;
import java.sql.ResultSet;
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
        String sql = "select ud.qty,ud.smallest_qty\n" + "from stock s join unit_relation_detail ud\n" + 
                "on s.rel_code = ud.rel_code\n" + "and s.comp_code =ud.comp_code\n" + "and s.dept_id =ud.dept_id\n" + 
                "where s.stock_code ='" + stockCode + "'\n" + "and s.comp_code ='" + compCode + "'\n" + 
                "and s.dept_id =" + deptId + "\n" + "and ud.unit ='" + unit + "'";
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
}
