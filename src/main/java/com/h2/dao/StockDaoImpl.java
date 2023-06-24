/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.Stock;
import com.inventory.model.StockKey;
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
public class StockDaoImpl extends AbstractDao<StockKey, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public List<Stock> findAll() {
        String hsql = "select o from Stock o";
        return findHSQL(hsql);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        String hsql = "select o from Stock o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Stock o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Stock> getStock(String str, String compCode,Integer deptId) {
        str = Util1.cleanString(str);
        List<Stock> list = getStockList("LOWER(REPLACE(s.user_code, ' ', '')) like '" + str + "%'", compCode);
        if (list.isEmpty()) {
            list = getStockList("LOWER(REPLACE(s.stock_name, ' ', '')) like '" + str + "%'", compCode);
        }
        return list;
    }

    private List<Stock> getStockList(String filter, String compCode) {
        List<Stock> listStock = new ArrayList<>();
        String sql = """
        select s.*,rel.rel_name,st.stock_type_name,cat.cat_name,b.brand_name
        from stock s
        join unit_relation rel on s.rel_code= rel.rel_code
        left join stock_type st on s.stock_type_code = st.stock_type_code
        left join category cat  on s.category_code = cat.cat_code
        left join stock_brand b on s.brand_code  = b.brand_code
        where s.comp_code ='""" + compCode + "'\n" + "and s.active =true\n"
                + "and " + filter + "\n"
                + "order by s.user_code,s.stock_name\n" + "limit 100";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //stock_code, active, brand_code, stock_name, category_code, stock_type_code, created_by,
                    // created_date, updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date,
                    // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b, sale_price_c,
                    // sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code, comp_code, user_code, mac_id,
                    // rel_code, calculate, dept_id, rel_name, stock_type_name, cat_name, brand_name
                    Stock s = new Stock();
                    StockKey key = new StockKey();
                    key.setStockCode(rs.getString("stock_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    s.setKey(key);
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setBrandCode(rs.getString("brand_code"));
                    s.setCatCode(rs.getString("category_code"));
                    s.setTypeCode(rs.getString("stock_type_code"));
                    s.setPurPrice(rs.getFloat("pur_price"));
                    s.setPurUnitCode(rs.getString("pur_unit"));
                    s.setSaleUnitCode(rs.getString("sale_unit"));
                    s.setWeightUnit(rs.getString("weight_unit"));
                    s.setWeight(rs.getFloat("weight"));
                    s.setSalePriceN(rs.getFloat("sale_price_n"));
                    s.setSalePriceA(rs.getFloat("sale_price_a"));
                    s.setSalePriceB(rs.getFloat("sale_price_b"));
                    s.setSalePriceC(rs.getFloat("sale_price_c"));
                    s.setSalePriceD(rs.getFloat("sale_price_d"));
                    s.setSalePriceE(rs.getFloat("sale_price_e"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setUserCode(rs.getString("user_code"));
                    s.setRelName(rs.getString("rel_name"));
                    s.setGroupName(rs.getString("stock_type_name"));
                    s.setCatName(rs.getString("cat_name"));
                    s.setBrandName(rs.getString("brand_name"));
                    s.setExplode(rs.getBoolean("explode"));
                    listStock.add(s);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return listStock;
    }

    @Override
    public Stock find(StockKey key) {
        return getByKey(key);
    }

}
