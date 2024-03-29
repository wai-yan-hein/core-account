/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.entity.SaleDetailKey;
import com.inventory.entity.SaleHisDetail;
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
public class SaleHisDetailDaoImpl extends AbstractDao<SaleDetailKey, SaleHisDetail> implements SaleHisDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail cat) {
        saveOrUpdate(cat, cat.getKey());
        return cat;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from SaleHisDetail o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<SaleHisDetail> findAll(String compCode) {
        String hsql = "select o from SaleHisDetail o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public SaleHisDetail find(SaleDetailKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(SaleDetailKey key) {
        remove(key);
    }

    @Override
    public List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId) {
        String hsql = "select o from SaleHisDetail o where o.key.compCode ='" + compCode + "' "
                + "and o.key.vouNo = '" + vouNo + "' and o.key.deptId = " + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public List<SaleHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<SaleHisDetail> listOP = new ArrayList<>();
        String sql = """
                     select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name,t.trader_name
                     from sale_his_detail op
                     join location l on op.loc_code = l.loc_code
                     and op.comp_code = l.comp_code
                     and op.dept_id = l.dept_id
                     join stock s on op.stock_code = s.stock_code
                     and op.comp_code = s.comp_code
                     and op.dept_id = s.dept_id
                     join unit_relation rel on s.rel_code = rel.rel_code
                     and op.comp_code = rel.comp_code
                     and op.dept_id = rel.dept_id
                     left join stock_type st  on s.stock_type_code = st.stock_type_code
                     and op.comp_code = st.comp_code
                     and op.dept_id = st.dept_id
                     left join category cat on s.category_code = cat.cat_code
                     and op.comp_code = cat.comp_code
                     and op.dept_id = cat.dept_id
                     left join stock_brand sb on s.brand_code = sb.brand_code
                     and op.comp_code = sb.comp_code
                     and op.dept_id = sb.dept_id
                     left join grn g on op.batch_no = g.batch_no
                     and op.comp_code = g.comp_code
                     and op.dept_id = g.dept_id
                     and g.deleted = false
                     left join trader t on g.trader_code = t.code
                     and g.comp_code = t.comp_code
                     and g.dept_id = t.dept_id
                     where op.vou_no ='""" + vouNo + "'\n" + "and op.comp_code ='" + compCode + "'\n" + "and op.dept_id = " + deptId + "\n" + "order by op.unique_id";

        try {
            ResultSet rs = getResult(sql);
            if (rs != null) {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    SaleHisDetail op = new SaleHisDetail();
                    SaleDetailKey key = new SaleDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setWeight(rs.getDouble("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setStdWeight(rs.getDouble("std_weight"));
                    op.setQty(rs.getDouble("qty"));
                    op.setPrice(rs.getDouble("sale_price"));
                    op.setAmount(rs.getDouble("sale_amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("sale_unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    op.setBatchNo(rs.getString("batch_no"));
                    op.setTraderName(rs.getString("trader_name"));
                    listOP.add(op);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return listOP;
    }

}
