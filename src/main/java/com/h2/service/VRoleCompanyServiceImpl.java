package com.h2.service;
import com.h2.dao.VRoleCompanyDao;
import com.user.model.VRoleCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VRoleCompanyServiceImpl implements VRoleCompanyService{
    @Autowired
    private VRoleCompanyDao dao;
    @Override
    public List<VRoleCompany> getPrivilegeCompany(String roleCode) {
        return dao.getPrivilegeCompany(roleCode);
    }
}
