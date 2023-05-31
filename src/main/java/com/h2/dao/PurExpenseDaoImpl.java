package com.h2.dao;

import com.inventory.model.PurExpense;
import com.inventory.model.PurExpenseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class PurExpenseDaoImpl extends AbstractDao<PurExpenseKey, PurExpense> implements PurExpenseDao {
    @Override
    public PurExpense save(PurExpense p) {
        saveOrUpdate(p,p.getKey());
        return p;
    }

    @Override
    public List<PurExpense> search(String vouNo, String compCode) {
        String sql = "select a.*,e.expense_name\n" +
                "from (\n" +
                "select *\n" +
                "from pur_expense\n" +
                "where vou_no ='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                ")a\n" +
                "join expense e\n" +
                "on a.expense_code = e.expense_code\n" +
                "and a.comp_code = e.comp_code\n" +
                "order by a.unique_id";
        ResultSet rs = getResult(sql);
        List<PurExpense> list = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    PurExpense e = new PurExpense();
                    PurExpenseKey key = new PurExpenseKey();
                    key.setExpenseCode(rs.getString("expense_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    e.setKey(key);
                    e.setExpenseName(rs.getString("expense_name"));
                    e.setAmount(rs.getFloat("amount"));
                    list.add(e);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return list;
    }
}
