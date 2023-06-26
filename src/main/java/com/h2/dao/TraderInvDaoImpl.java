/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Trader;
import com.inventory.model.TraderKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
@Slf4j
public class TraderInvDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderInvDao {

    @Override
    public Trader save(Trader cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Trader o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Trader> findAll(String compCode) {
        String hsql = "select o from Trader o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public Trader find(TraderKey key) {
        return getByKey(key);
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode, Integer deptId) {
        String filter = "where active = TRUE\n"
                + "and deleted = FALSE\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n"
                + "and (LOWER(REPLACE(user_code, ' ', '')) like '" + str + "%' or LOWER(REPLACE(trader_name, ' ', '')) like '" + str + "%') \n";
        if (!type.equals("-")) {
            filter += "and (multi = TRUE or type ='" + type + "')";
        }
        String sql = "select code,user_code,trader_name,price_type,type,address\n"
                + "from trader\n" + filter + "\n"
                + "order by user_code,trader_name\n"
                + "limit 100\n";
        ResultSet rs = getResult(sql);
        List<Trader> list = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Trader t = new Trader();
                    TraderKey key = new TraderKey();
                    key.setCompCode(compCode);
                    key.setCode(rs.getString("code"));
                    t.setKey(key);
                    t.setDeptId(deptId);
                    t.setUserCode(rs.getString("user_code"));
                    t.setTraderName(rs.getString("trader_name"));
                    t.setPriceType(rs.getString("price_type"));
                    t.setType(rs.getString("type"));
                    t.setAddress(rs.getString("address"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return list;
    }

}
