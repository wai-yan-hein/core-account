package com.h2.service;

import com.user.model.CompanyInfo;
import java.util.List;

public interface VRoleCompanyService {
    List<CompanyInfo> getPrivilegeCompany(String roleCode);
}
