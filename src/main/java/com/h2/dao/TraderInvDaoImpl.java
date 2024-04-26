/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderKey;
import jakarta.persistence.Query;
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
        str = Util1.cleanStr(str);
        str = str + "%";
        String filter = """
                where active = true
                and deleted = false
                and comp_code =?
                and (dept_id =? or 0 =?)
                and (LOWER(REPLACE(user_code, ' ', '')) like ? or LOWER(REPLACE(trader_name, ' ', '')) like ?)
                """;
        if (!type.equals("-")) {
            filter += "and (multi =true or type ='" + type + "')";
        }
        String sql = """
                     select code,user_code,trader_name,price_type,type,address,credit_amt,credit_days,account
                     from trader
                     """ + filter + "\n"
                + "order by user_code,trader_name\n"
                + "limit 100\n";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, str, str);
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
                    t.setCreditAmt(rs.getFloat("credit_amt"));
                    t.setCreditDays(rs.getInt("credit_days"));
                    t.setAccount(rs.getString("account"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Trader> getTrader(String compCode, String type) {
        String hsql = """
                    select o
                    from Trader o
                    where o.key.compCode =:compCode
                    and o.type =:type
                    and deleted = false
                    order by o.userCode
                    """;
        Query query = getEntityManager().createQuery(hsql);
        query.setParameter("compCode", compCode);
        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    public Boolean delete(TraderKey key) {
        Trader t = find(key);
        if (t != null) {
            t.setDeleted(true);
            t.setUpdatedDate(LocalDateTime.now());
            update(t);
        }
        return true;
    }

}
