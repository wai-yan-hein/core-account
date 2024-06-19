/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.DepartmentKey;
import com.user.model.Branch;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Repository
@Service
@Transactional
@Slf4j
public class BranchRepo extends AbstractDao<DepartmentKey, Branch> {

    public Branch save(Branch dept) {
        saveOrUpdate(dept, dept.getKey());
        return dept;
    }

    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from Branch o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    public Branch findById(DepartmentKey id) {
        return getByKey(id);
    }

    public List<Branch> findAll(Boolean active, String compCode) {
        String sql;
        if (active) {
            sql = "select o from Branch o where o.deleted =false and o.active =true and o.key.compCode ='" + compCode + "'";
        } else {
            sql = "select o from Branch o where o.deleted =false and o.key.compCode ='" + compCode + "'";
        }
        return findHSQL(sql);
    }

}
