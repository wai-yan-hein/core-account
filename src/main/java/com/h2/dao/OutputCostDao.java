/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 package com.h2.dao;

import com.inventory.model.OutputCost;
import com.inventory.model.OutputCostKey;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author pann
 */
public interface OutputCostDao {

    OutputCost findByCode(OutputCostKey key);

    OutputCost save(OutputCost item);

    List<OutputCost> findAll(String compCode);

    List<OutputCost> search(String catName);

    List<OutputCost> unUploadOutputCost();

    boolean delete(OutputCostKey key);

    String getMaxDate();

    List<OutputCost> getOutputCost(LocalDateTime updatedDate);

}
