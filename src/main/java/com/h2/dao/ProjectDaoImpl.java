/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.util.Date;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class ProjectDaoImpl extends AbstractDao<ProjectKey, Project> implements ProjectDao {

    @Override
    public Project save(Project p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from Project o";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

}
