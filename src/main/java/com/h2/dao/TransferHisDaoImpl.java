package com.h2.dao;

import com.common.Util1;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.VTransfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class TransferHisDaoImpl extends AbstractDao<TransferHisKey, TransferHis> implements TransferHisDao {

    @Autowired
    private TransferHisDetailDao dao;

    @Override
    public TransferHis save(TransferHis th) {
        saveOrUpdate(th, th.getKey());
        return th;
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<TransferHis> unUpload(String compCode) {
        String hsql = "select o from TransferHis o where o.key.compCode = '" + compCode + "' and o.intgUpdStatus is null";
        List<TransferHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
//            String compCode1 = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListTD(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public void delete(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update transfer_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);
    }

    @Override
    public void restore(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update transfer_his set deleted =0 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSql(sql);
    }

    @Override
    public List<TransferHis> search(String updatedDate, List<String> location) {
        List<TransferHis> list = new ArrayList<>();
        if (location != null) {
            for (String locCode : location) {
                //vou_no, created_by, created_date, deleted, vou_date, ref_no, remark, updated_by,
                // updated_date, loc_code_from, loc_code_to, mac_id, comp_code, dept_id, intg_upd_status
                String sql = "select * from transfer_his where (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "') and intg_upd_status is null";
                try {
                    ResultSet rs = getResult(sql);
                    if (rs != null) {
                        while (rs.next()) {
                            TransferHis th = new TransferHis();
                            TransferHisKey key = new TransferHisKey();
                            key.setVouNo(rs.getString("vou_no"));
                            key.setDeptId(rs.getInt("dept_id"));
                            key.setCompCode(rs.getString("comp_code"));
                            th.setKey(key);
                            th.setCreatedBy(rs.getString("created_by"));
                            th.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                            th.setDeleted(rs.getBoolean("deleted"));
                            th.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                            th.setRefNo(rs.getString("ref_no"));
                            th.setRemark(rs.getString("remark"));
                            th.setUpdatedBy(rs.getString("updated_by"));
                            th.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                            th.setLocCodeFrom(rs.getString("loc_code_from"));
                            th.setLocCodeTo(rs.getString("loc_code_to"));
                            th.setMacId(rs.getInt("mac_id"));
                            th.setIntgUpdStatus(rs.getString("intg_upd_status"));
                            list.add(th);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListTD(dao.searchDetail(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql1 = "delete from transfer_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql2 = "delete from transfer_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        execSql(sql1, sql2);
    }

    @Override
    public TransferHis updateACK(TransferHisKey key) {
        TransferHis th = getByKey(key);
        th.setIntgUpdStatus("ACK");
        saveOrUpdate(th, key);
        return th;
    }

    @Override
    public List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted) {
        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'\n";
        }
        if (!refNo.equals("-")) {
            filter += "and ref_no like '" + refNo + "%'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by ='" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "')\n";
        }
        String sql = """
                     select a.*,l.loc_name as from_loc_name, ll.loc_name as to_loc_name, 
                                          from (select * 
                                          from transfer_his
                     """
                + "\n where comp_code='" + compCode + "' and dept_id = " + deptId
                + "\n and CAST(vou_date AS DATE) between  '" + fromDate + "' and '" + toDate + "'"
                + "\n and deleted = " + Boolean.parseBoolean(deleted)
                + "\n and intg_upd_status is null)a"
                + "\n join location l"
                + "\n on a.loc_code_from = l.loc_code"
                + "\n and a.comp_code = l.comp_code"
                + "\n join location ll on a.loc_code_to = ll.loc_code"
                + "\n and a.comp_code = ll.comp_code";
        List<VTransfer> openingList = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VTransfer s = new VTransfer();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setRemark(rs.getString("remark"));
                    s.setRefNo(rs.getString("ref_no"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setFromLocationName(rs.getString("from_loc_name"));
                    s.setToLocationName(rs.getString("to_loc_name"));
                    s.setDeptId(rs.getInt("dept_id"));
                    openingList.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return openingList;
    }
}
