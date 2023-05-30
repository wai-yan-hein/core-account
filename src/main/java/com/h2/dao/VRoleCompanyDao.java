package com.h2.dao;

import com.user.model.VRoleCompany;
import java.util.List;

public interface VRoleCompanyDao {
    List<VRoleCompany> getPrivilegeCompany(String roleCode);
}
