package com.h2.service;
import com.h2.dao.VRoleCompanyDao;
import com.user.model.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Lazy
@Service
@Transactional
public class VRoleCompanyServiceImpl implements VRoleCompanyService{
    @Autowired
    private VRoleCompanyDao dao;
    @Override
    public List<CompanyInfo> getPrivilegeCompany(String roleCode) {
        return dao.getPrivilegeCompany(roleCode);
    }
}
