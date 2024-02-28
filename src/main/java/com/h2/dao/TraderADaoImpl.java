/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.common.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class TraderADaoImpl extends AbstractDao<TraderAKey, TraderA> implements TraderADao {

    @Override
    public TraderA save(TraderA trader) {
        saveOrUpdate(trader, trader.getKey());
        return trader;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from TraderA o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<TraderA> findAll(String compCode) {
        String hsql = "select o from TraderA o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<TraderA> getTrader(String text, String compCode) {
        text = Util1.cleanStr(text);
        text = text + "%";
        String filter = """
                where active = true
                and deleted = false
                and comp_code = ?
                and (LOWER(REPLACE(user_code, ' ', '')) LIKE ? OR LOWER(REPLACE(trader_name, ' ', '')) LIKE ?)
                """;
        String sql = """
                     SELECT code AS trader_code, user_code, trader_name, account_code, discriminator
                     FROM trader_acc
                     """
                + filter + "\n"
                + "ORDER BY user_code, trader_name\n"
                + "LIMIT 100";
        ResultSet rs = getResult(sql, compCode, text, text);
        List<TraderA> list = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    TraderA t = new TraderA();
                    TraderAKey key = new TraderAKey();
                    key.setCompCode(compCode);
                    key.setCode(rs.getString("trader_code"));
                    t.setKey(key);
                    t.setUserCode(rs.getString("user_code"));
                    t.setTraderName(rs.getString("trader_name"));
                    t.setAccount(rs.getString("account_code"));
                    t.setTraderType(rs.getString("discriminator"));
                    list.add(t);
                }
            }
        } catch (SQLException ignored) {
        }
        return list;
    }

}
