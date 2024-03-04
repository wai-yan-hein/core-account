package com.h2.dao;

import com.inventory.entity.THDetailKey;
import com.inventory.entity.TransferHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class TransferHisDetailDaoImpl extends AbstractDao<THDetailKey, TransferHisDetail> implements TransferHisDetailDao {

    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        saveOrUpdate(th, th.getKey());
        return th;
    }

    @Override
    public int delete(THDetailKey key) {
        remove(key);
        return 1;
    }

    @Override
    public List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<TransferHisDetail> list = new ArrayList<>();
        String sql = "select td.*,s.user_code,s.stock_name,rel.rel_name\n" + "from transfer_his_detail td \n" + "join stock s on td.stock_code = s.stock_code\n" + "and td.comp_code = s.comp_code\n" + "and td.dept_id = s.dept_id\n" + "join unit_relation rel on s.rel_code = rel.rel_code\n" + "and td.comp_code = rel.comp_code\n" + "and td.dept_id = rel.dept_id\n" + "where td.vou_no ='" + vouNo + "'\n" + "and td.comp_code ='" + compCode + "'\n" + "and td.dept_id = " + deptId + "\n" + "order by td.unique_id";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //td_code, vou_no, stock_code, qty, unit, unique_id, comp_code, dept_id, stock_name, rel_name
                    TransferHisDetail td = new TransferHisDetail();
                    THDetailKey key = new THDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setCompCode(rs.getString("comp_code"));
                    td.setKey(key);
                    td.setDeptId(rs.getInt("dept_id"));
                    td.setUserCode(rs.getString("user_code"));
                    td.setStockCode(rs.getString("stock_code"));
                    td.setStockName(rs.getString("stock_name"));
                    td.setQty(rs.getFloat("qty"));
                    td.setUnitCode(rs.getString("unit"));
                    td.setRelName(rs.getString("rel_name"));
                    list.add(td);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    @Override
    public List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId) {
        String sql = "select o from TransferHisDetail o where o.key.compCode = '" + compCode + "'"
                + " and o.key.vouNo = '" + vouNo + "' and o.key.deptId = " + deptId;
        return findHSQL(sql);
    }
}
