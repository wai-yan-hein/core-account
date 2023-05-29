/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.Project;
import com.user.model.ProjectKey;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface ProjectDao {

    Project save(Project p);

    String getMaxDate();

    List<Project> searchProject(String compCode);

    Project findById(ProjectKey key);

    List<Project> search(String str, String compCode);
}
