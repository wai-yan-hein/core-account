/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.ProjectDao;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectDao dao;

    @Override
    public Project save(Project pc) {
        return dao.save(pc);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Project> searchProject(String compCode) {
        return dao.searchProject(compCode);
    }

    @Override
    public Project findById(ProjectKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Project> search(String str, String compCode) {
        return dao.search(str, compCode);
    }

}
