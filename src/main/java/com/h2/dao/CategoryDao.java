/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.inventory.entity.Category;
import com.inventory.entity.CategoryKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface CategoryDao {

    Category save(Category stock);

    Category find(CategoryKey key);

    String getMaxDate();

    List<Category> findAll(String compCode);

}
