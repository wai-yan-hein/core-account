/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface GlDao {

    Gl save(Gl coa);

    String getMaxDate();

    List<Gl> findAll(String compCode);

    Gl findById(GlKey key);
    
    boolean delete(GlKey key, String modifyBy);
  
    Gl findByCode(GlKey key);
    
    Gl findWithSql(GlKey key);
    
    boolean deleteInvVoucher(String refNo, String tranSource, String compCode);

    boolean deleteVoucher(String glVouNo, String compCode);
    
    List<Gl> unUploadVoucher(String compCode);
    
    Gl updateACK(GlKey key);
}
