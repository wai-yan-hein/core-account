/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.inventory.model.Category;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface CategoryService {

    Category save(Category stock);

    List<Category> findAll(String compCode);

    String getMaxDate();

}
