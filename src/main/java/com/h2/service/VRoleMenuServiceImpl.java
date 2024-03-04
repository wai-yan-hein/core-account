package com.h2.service;
import com.h2.dao.VRoleMenuDao;
import com.inventory.entity.VRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Lazy
@Service
@Transactional
public class VRoleMenuServiceImpl implements VRoleMenuService{
    @Autowired
    private VRoleMenuDao dao;
    @Override
    public List<VRoleMenu> getMenu(String roleCode, String parentCode, String compCode,boolean privilege) {
        return dao.getMenu(roleCode, parentCode, compCode,privilege);
    }

    @Override
    public List<VRoleMenu> getReport(String roleCode, String menuClass, String compCode) {
        return dao.getReport(roleCode, menuClass, compCode);
    }
}
