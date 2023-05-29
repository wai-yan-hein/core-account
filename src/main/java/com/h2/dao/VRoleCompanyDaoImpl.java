package com.h2.dao;

import com.user.model.VRoleCompany;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VRoleCompanyDaoImpl extends AbstractDao<String, VRoleCompany> implements VRoleCompanyDao {
    @Override
    public List<VRoleCompany> getPrivilegeCompany(String roleCode) {
        String sql = """
                     select o.role_code AS role_code,o.comp_code AS comp_code,o.allow AS allow,o.name AS name,
                     o.phone AS phone,o.address AS address,o.start_date AS start_date,o.end_date AS end_date,o.currency AS currency,
                     o.batch_lock AS batch_lock,o.year_end_date AS year_end_date,o.active AS active 
                     from (
                     select p.role_code AS role_code,p.comp_code AS comp_code,p.allow AS allow,com.name AS name,
                     com.phone AS phone,com.address AS address,com.start_date AS start_date,com.end_date AS end_date,com.currency AS currency,
                     com.batch_lock AS batch_lock,com.year_end_date AS year_end_date,com.active AS active
                     from (privilege_company p join company_info com on(p.comp_code = com.comp_code))) o
                     where o.role_code = '""" + roleCode + "' and o.allow = TRUE and o.active = TRUE";
        ResultSet rs = getResult(sql);
        List<VRoleCompany> vList = new ArrayList<>();
        VRoleCompany vRole;
        if (rs != null) {
            try {
                while (rs.next()) {
                    vRole = new VRoleCompany();
                    vRole.setRoleCode(rs.getString("role_code"));
                    vRole.setCompCode(rs.getString("comp_code"));
                    vRole.setAllow(rs.getBoolean("allow"));
                    vRole.setCompName(rs.getString("name"));
                    vRole.setCompPhone(rs.getString("phone"));
                    vRole.setCompAddress(rs.getString("address"));
                    vRole.setStartDate(rs.getDate("start_date"));
                    vRole.setEndDate(rs.getDate("end_date"));
                    vRole.setCurrency(rs.getString("currency"));
                    vRole.setBatchLock(rs.getBoolean("batch_lock"));
                    vRole.setYearEndDate(rs.getDate("year_end_date"));
                    vRole.setActive(rs.getBoolean("active"));
                    vList.add(vRole);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vList;
    }
}
