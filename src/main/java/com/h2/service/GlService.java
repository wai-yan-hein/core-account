/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.ReturnObject;
import java.util.List;

/**
 *
 * @author dell
 */
public interface GlService {
    
    ReturnObject save(List<Gl> gl);

    Gl save(Gl gl, boolean backup);

    Gl findById(GlKey key);

    List<Gl> findAll(String compCode);

    String getMaxDate();
    
    Gl findByCode(GlKey key);
    
    Gl updateACK(GlKey key);
    
    List<Gl> unUploadVoucher(String compCode);

}
