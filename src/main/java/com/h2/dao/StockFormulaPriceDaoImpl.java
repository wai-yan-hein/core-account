package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class StockFormulaPriceDaoImpl extends AbstractDao<StockFormulaPriceKey, StockFormulaPrice> implements StockFormulaPriceDao {

    @Override
    public StockFormulaPrice save(StockFormulaPrice s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public boolean delete(StockFormulaPriceKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<StockFormulaPrice> getStockFormulaPrice(String code, String compCode) {
         String sql = """
                select s.*,sc.criteria_name,sc.user_code
                from stock_formula_price s
                join stock_criteria sc on s.criteria_code = sc.criteria_code
                and s.comp_code = s.comp_code
                where s.comp_code =?
                and s.formula_code = ?
                order by s.unique_id
                """;
        ResultSet rs = getResult(sql, compCode, code);
        List<StockFormulaPrice> list = new ArrayList<>();
        try {
            while (rs.next()) {
                //formula_code, comp_code, unique_id, criteria_code, percent, price
                StockFormulaPrice d = new StockFormulaPrice();
                StockFormulaPriceKey key = new StockFormulaPriceKey();
                key.setFormulaCode(rs.getString("formula_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setKey(key);
                d.setCriteriaCode(rs.getString("criteria_code"));
                d.setUserCode(rs.getString("user_code"));
                d.setCriteriaName(rs.getString("criteria_name"));
                d.setPercent(rs.getDouble("percent"));
                d.setPrice(rs.getDouble("price"));
                d.setPercentAllow(rs.getDouble("percent_allow"));
                list.add(d);
            }
        } catch (SQLException e) {
            log.error("getStockFormulaPrice : "+e.getMessage());
        }
        return list;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from StockFormulaPrice o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
}
