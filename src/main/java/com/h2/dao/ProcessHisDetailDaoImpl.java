package com.h2.dao;

import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ProcessHisDetailDaoImpl extends AbstractDao<ProcessHisDetailKey, ProcessHisDetail> implements ProcessHisDetailDao {

    @Override
    public ProcessHisDetail save(ProcessHisDetail ph) {
        saveOrUpdate(ph, ph.getKey());
        return ph;
    }

    @Override
    public ProcessHisDetail findById(ProcessHisDetailKey key) {
        return getByKey(key);
    }

//    @Override
//    public List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId) {
//        List<ProcessHisDetail> list = new ArrayList<>();
//        String sql = "select a.*,s.user_code,s.stock_name,l.loc_name\n"
//                + "from (\n"
//                + "select *\n"
//                + "from process_his_detail\n"
//                + "where vou_no ='" + vouNo + "'\n"
//                + "and comp_code ='" + compCode + "'\n"
//                + "and dept_id =" + deptId + "\n"
//                + ")a\n"
//                + "join stock s on s.stock_code = a.stock_code\n"
//                + "and s.comp_code =a.comp_code\n"
//                + "and s.dept_id = a.dept_id\n"
//                + "join location l on a.loc_code = l.loc_code\n"
//                + "and l.comp_code =a.comp_code\n"
//                + "and l.dept_id = a.dept_id\n"
//                + "order by a.unique_id\n";
//        ResultSet rs = getResult(sql);
//        try {
//            while (rs.next()) {
//                ProcessHisDetail p = new ProcessHisDetail();
//                ProcessHisDetailKey key = new ProcessHisDetailKey();
//                key.setCompCode(compCode);
//                key.setVouNo(vouNo);
//                key.setDeptId(deptId);
//                key.setStockCode(rs.getString("stock_code"));
//                key.setLocCode(rs.getString("loc_code"));
//                key.setUniqueId(rs.getInt("unique_id"));
//                p.setKey(key);
//                p.setLocName(rs.getString("loc_name"));
//                p.setStockName(rs.getString("stock_name"));
//                p.setStockUsrCode(rs.getString("user_code"));
//                p.setQty(rs.getFloat("qty"));
//                p.setPrice(rs.getFloat("price"));
//                p.setUnit(rs.getString("unit"));
//                p.setVouDate(rs.getDate("vou_date"));
//                list.add(p);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return list;
//    }
    @Override
    public void delete(ProcessHisDetailKey key) {
        String sql = "delete from process_his_detail where vou_no='" + key.getVouNo() + "'\n"
                + "and unique_id =" + key.getUniqueId() + " and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSql(sql);
    }

    @Override
    public List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId) {
        String hsql = "select o from OrderHisDetail o where o.key.compCode ='" + compCode + "' "
                + "and o.key.vouNo = '" + vouNo + "' and o.key.deptId = " + deptId + "";
        return findHSQL(hsql);
    }
}
