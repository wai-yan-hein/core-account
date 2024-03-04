package com.h2.dao;

import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import java.util.List;

public interface GradeDetailDao {

    GradeDetail save(GradeDetail s);

    boolean delete(GradeDetailKey key);

    List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode);

    List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode);

    String getMaxDate();
}
