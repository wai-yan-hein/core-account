/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        LocalDateTime date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<Project> searchProject(String compCode) {
        String hsql = "select o from Project o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public Project findById(ProjectKey key) {
        return getByKey(key);
    }

    @Override
    public List<Project> search(String str, String compCode) {
        String sql = "select project_no,project_name,comp_code\n"
                + "from project\n"
                + "where comp_code ='" + compCode + "'\n"
                + "and project_status='PROGRESS'\n"
                + "and (project_no like '" + str + "%' or project_name like '" + str + "%')\n"
                + "order by project_no,project_name";
        List<Project> list = new ArrayList<>();
        List<Map<String, Object>> hm = getList(sql);
        hm.forEach(obj -> {
            Project p = new Project();
            ProjectKey key = new ProjectKey();
            key.setProjectNo(Util1.getString(obj.get("project_no")));
            key.setCompCode(Util1.getString(obj.get("comp_code")));
            p.setKey(key);
            p.setProjectName(Util1.getString(obj.get("project_name")));
            list.add(p);
        });
        return list;
    }

}
