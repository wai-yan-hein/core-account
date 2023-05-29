package com.h2.service;

import com.user.model.VRoleCompany;
import java.util.List;

public interface VRoleCompanyService {
    List<VRoleCompany> getPrivilegeCompany(String roleCode);
}
