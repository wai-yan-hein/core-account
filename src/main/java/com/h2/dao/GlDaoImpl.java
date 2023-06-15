/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.Util1;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dell
 */
@Repository
@Slf4j
public class GlDaoImpl extends AbstractDao<GlKey, Gl> implements GlDao {

    @Override
    public Gl save(Gl gl) {
        saveOrUpdate(gl, gl.getKey());
        return gl;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.modifyDate) from Gl o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Gl> findAll(String compCode) {
        String hsql = "select o from Gl o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public Gl findById(GlKey key) {
        return getByKey(key);
    }
    
    @Override
    public Gl findByCode(GlKey key) {
        return getByKey(key);
    }
    
    @Override
    public boolean delete(GlKey key, String modifyBy) {
        String sql = "update gl\n" + " set deleted =1,intg_upd_status = null,modify_by ='" + modifyBy + "'\n" + " where gl_code = '" + key.getGlCode() + "'\n" + " and comp_code ='" + key.getCompCode() + "'\n" + " and dept_id =" + key.getDeptId() + "";
        execSql(sql);
        return true;
    }
    
     @Override
    public boolean deleteInvVoucher(String refNo, String tranSource, String compCode) {
        String sql = "update gl set deleted =1,intg_upd_status = null where ref_no ='" + refNo + "' and tran_source='" + tranSource + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }

    @Override
    public boolean deleteVoucher(String glVouNo, String compCode) {
        String sql = "update gl set deleted =1 where gl_vou_no ='" + glVouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql);
        return true;
    }
    
    @Override
    public Gl findWithSql(GlKey key) {
        try {
            String sql = "select * from gl where gl_code='" + key.getGlCode() + "' and comp_code ='" + key.getCompCode() + "'";
            List<Map<String, Object>> result = getList(sql);
            if (!result.isEmpty()) {
                Map<String, Object> rs = result.get(0);
                Gl gl = new Gl();
                gl.setKey(key);
                gl.setGlDate(Util1.toDate(rs.get("gl_date")));
                gl.setCreatedDate(Util1.toDate(rs.get("created_date")));
                gl.setModifyDate(Util1.toDate(rs.get("modify_date")));
                gl.setModifyBy(Util1.getString(rs.get("modify_by")));
                gl.setDescription(Util1.getString(rs.get("description")));
                gl.setSrcAccCode(Util1.getString(rs.get("source_ac_id")));
                gl.setAccCode(Util1.getString(rs.get("account_id")));
                gl.setCurCode(Util1.getString(rs.get("cur_code")));
                gl.setDrAmt(Util1.getDouble(rs.get("dr_amt")));
                gl.setCrAmt(Util1.getDouble(rs.get("cr_amt")));
                gl.setReference(Util1.getString(rs.get("reference")));
                gl.setDeptCode(Util1.getString(rs.get("dept_code")));
                gl.setVouNo(Util1.getString(rs.get("voucher_no")));
                gl.setTraderCode(Util1.getString(rs.get("trader_code")));
                gl.setTranSource(Util1.getString(rs.get("tran_source")));
                gl.setGlVouNo(Util1.getString(rs.get("gl_vou_no")));
                gl.setRemark(Util1.getString(rs.get("remark")));
                gl.setRefNo(Util1.getString(rs.get("ref_no")));
                gl.setMacId(Util1.getInteger(rs.get("mac_id")));
                return gl;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Gl> unUploadVoucher(String compCode) {
        String hsql = "select o from Gl o where o.intgUpdStatus is null";
        List<Gl> list = findHSQL(hsql);
        return list;
    }

    @Override
    public Gl updateACK(GlKey key) {
        Gl sh = getByKey(key);
        sh.setIntgUpdStatus("ACK");
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }
}
