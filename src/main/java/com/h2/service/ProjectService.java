/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.Project;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface ProjectService {

    Project save(Project pc);

    String getMaxDate();
    
    List<Project> searchProject(String compCode);
}
