/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class COADaoImpl extends AbstractDao<COAKey, ChartOfAccount> implements COADao {

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        saveOrUpdate(coa, coa.getKey());
        return coa;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.modifiedDate) from ChartOfAccount o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<ChartOfAccount> findAll(String compCode) {
        String hsql = "select o from ChartOfAccount o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public ChartOfAccount findById(COAKey key) {
        return getByKey(key);
    }

    @Override
    public List<ChartOfAccount> getCOA(String headCode, String compCode) {
        String sql = "select coa.coa_code,coa.comp_code,coa.coa_name_eng\n" + "from chart_of_account coa join(\n"
                + "select coa_code,comp_code\n" + "from chart_of_account\n" + "where coa_parent ='" + headCode + "'\n"
                + "and comp_code ='" + compCode + "'\n" + ")a\n" + "on coa.coa_parent = a.coa_code\n" + "and coa.comp_code = a.comp_code\n" + "where coa.coa_level =3";
        List<Map<String, Object>> result = getList(sql);
        List<ChartOfAccount> list = new ArrayList<>();
        result.forEach((row) -> {
            ChartOfAccount c = new ChartOfAccount();
            COAKey key = new COAKey();
            key.setCoaCode(Util1.getString(row.get("coa_code")));
            key.setCompCode(Util1.getString(row.get("comp_code")));
            c.setKey(key);
            c.setCoaNameEng(Util1.getString(row.get("coa_name_eng")));
            list.add(c);
        });
        return list;
    }

    @Override
    public List<ChartOfAccount> getCOATree(String compCode) {
        String hsql = "select o from ChartOfAccount o where  o.coaParent = '#' and o.key.compCode = '" + compCode + "' and o.deleted =false";
        List<ChartOfAccount> chart = findHSQL(hsql);
        for (ChartOfAccount coa : chart) {
            getChild(coa, compCode);
        }
        return chart;
    }

    @Override
    public List<ChartOfAccount> getTraderCOA(String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        String sql = "select a.*,coa.coa_code_usr,coa.coa_name_eng,coa1.coa_name_eng group_name\n" + "from (\n" + "select distinct account_code,comp_code\n" + "from trader_acc \n" + "where comp_code='" + compCode + "' \n" + "and account_code is not null\n" + ")a\n" + "join chart_of_account coa on a.account_code = coa.coa_code\n" + "and a.comp_code = coa.comp_code\n" + "join chart_of_account coa1 on coa.coa_parent = coa1.coa_code\n" + "and coa.comp_code = coa1.comp_code";
        try {
            List<Map<String, Object>> result = getList(sql);
            result.forEach((rs) -> {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(Util1.getString(rs.get("account_code")));
                key.setCompCode(compCode);
                coa.setKey(key);
                coa.setCoaCodeUsr(Util1.getString(rs.get("coa_code_usr")));
                coa.setCoaNameEng(Util1.getString(rs.get("coa_name_eng")));
                coa.setGroupName(Util1.getString(rs.get("group_name")));
                list.add(coa);

            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChartOfAccount> getCOAChild(String parentCode, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parentCode + "' and o.key.compCode = '" + compCode + "' and o.deleted =false order by o.coaCodeUsr";
        return findHSQL(hsql);
    }

    private void getChild(ChartOfAccount parent, String compCode) {
        String hsql = "select o from ChartOfAccount o where o.coaParent = '" + parent.getKey().getCoaCode() + "' and o.key.compCode = '" + compCode + "' and o.deleted =false";
        List<ChartOfAccount> chart = findHSQL(hsql);
        parent.setChild(chart);
        if (!chart.isEmpty()) {
            for (ChartOfAccount coa : chart) {
                getChild(coa, compCode);
            }
        }
    }

    @Override
    public List<ChartOfAccount> searchCOA(String str, Integer level, String compCode) {
        List<ChartOfAccount> list = new ArrayList<>();
        str = Util1.cleanStr(str);
        str = str + "%";
        String sql = """
                     select a.*,c1.coa_code group_code,c1.coa_code_usr group_usr_code,c1.coa_name_eng group_name,c2.coa_code head_code,c2.coa_code_usr head_usr_code,c2.coa_name_eng head_name
                     from (
                     select coa_code,coa_code_usr,coa_name_eng,coa_parent,comp_code,coa_level
                     from chart_of_account
                     where active = true
                     and deleted = false
                     and (coa_level =? or 0 =?)
                     and comp_code =?
                     and (LOWER(REPLACE(coa_code_usr, ' ', '')) like ? or LOWER(REPLACE(coa_name_eng, ' ', '')) like ?)
                     limit 20
                     )a
                     left join chart_of_account c1
                     on a.coa_parent = c1.coa_code
                     and a.comp_code = c1.comp_code
                     left join chart_of_account c2
                     on c1.coa_parent = c2.coa_code
                     and c1.comp_code = c2.comp_code""";
        ResultSet rs = getResult(sql, level, level, compCode, str, str);
        try {
            while (rs.next()) {
                ChartOfAccount coa = new ChartOfAccount();
                COAKey key = new COAKey();
                key.setCoaCode(rs.getString("coa_code"));
                key.setCompCode(compCode);
                coa.setKey(key);
                coa.setCoaCodeUsr(rs.getString("coa_code_usr"));
                coa.setCoaNameEng(rs.getString("coa_name_eng"));
                coa.setGroupCode(rs.getString("group_code"));
                coa.setGroupUsrCode(rs.getString("group_usr_code"));
                coa.setGroupName(rs.getString("group_name"));
                coa.setHeadCode(rs.getString("head_code"));
                coa.setHeadUsrCode(rs.getString("head_usr_code"));
                coa.setHeadName(rs.getString("head_name"));
                coa.setCoaLevel(rs.getInt("coa_level"));
                list.add(coa);
            }
        } catch (SQLException e) {
            log.error("searchCOA : " + e.getMessage());
        }
        return list;
    }
}
