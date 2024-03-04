/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.inventory.entity.OutputCost;
import com.inventory.entity.OutputCostKey;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface OutputCostService {

    OutputCost findByCode(OutputCostKey key);

    OutputCost save(OutputCost outputCost);

    List<OutputCost> findAll(String compCode);

    boolean delete(OutputCostKey key);

    List<OutputCost> search(String catName);

    List<OutputCost> unUpload();

    String getMaxDate();

    List<OutputCost> getOutputCost(LocalDateTime updatedDate);
}
