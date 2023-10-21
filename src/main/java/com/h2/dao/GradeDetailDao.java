package com.h2.dao;

import com.inventory.model.GradeDetail;
import com.inventory.model.GradeDetailKey;
import java.util.List;

public interface GradeDetailDao {

    GradeDetail save(GradeDetail s);

    boolean delete(GradeDetailKey key);

    List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode);

    List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode);

    String getMaxDate();
}
