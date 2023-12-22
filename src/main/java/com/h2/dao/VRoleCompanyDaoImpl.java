package com.h2.dao;

import com.user.model.CompanyInfo;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Repository
public class VRoleCompanyDaoImpl extends AbstractDao<String, CompanyInfo> implements VRoleCompanyDao {

    @Override
    public List<CompanyInfo> getPrivilegeCompany(String roleCode) {
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
        List<CompanyInfo> vList = new ArrayList<>();
        CompanyInfo vRole;
        if (rs != null) {
            try {
                while (rs.next()) {
                    vRole = new CompanyInfo();
                    vRole.setRoleCode(rs.getString("role_code"));
                    vRole.setCompCode(rs.getString("comp_code"));
                    vRole.setAllow(rs.getBoolean("allow"));
                    vRole.setCompName(rs.getString("name"));
                    vRole.setCompPhone(rs.getString("phone"));
                    vRole.setCompAddress(rs.getString("address"));
                    vRole.setStartDate(rs.getObject("start_date",LocalDate.class));
                    vRole.setEndDate(rs.getObject("end_date",LocalDate.class));
                    vRole.setCurCode(rs.getString("currency"));
                    vRole.setBatchLock(rs.getBoolean("batch_lock"));
                    vRole.setYearEndDate(rs.getObject("year_end_date",LocalDate.class));
                    vRole.setActive(rs.getBoolean("active"));
                    vList.add(vRole);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return vList;
    }
}
