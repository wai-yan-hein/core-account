package com.h2.dao;

import com.user.model.CompanyInfo;
import java.util.List;

public interface VRoleCompanyDao {
    List<CompanyInfo> getPrivilegeCompany(String roleCode);
}
