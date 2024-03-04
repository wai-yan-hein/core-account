package com.h2.service;

import com.common.Util1;
import com.h2.dao.GradeDetailDao;
import com.h2.dao.StockFormulaDao;
import com.h2.dao.StockFormulaQtyDao;
import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import com.inventory.entity.StockFormulaKey;
import com.inventory.entity.StockFormulaQty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.h2.dao.StockFormulaPriceDao;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {

    private final StockFormulaDao formulaDao;
    private final StockFormulaPriceDao formulaPriceDao;
    private final GradeDetailDao gradeDetailDao;
    private final StockFormulaQtyDao formulaQtyDao;
    @Autowired
    private SeqService seqService;

    @Override
    public StockFormula save(StockFormula s) {
        if (Util1.isNullOrEmpty(s.getKey().getFormulaCode())) {
            s.getKey().setFormulaCode(getCode(s.getKey().getCompCode()));
            s.setCreatedDate(Util1.getTodayLocalDateTime());
        } else {
            s.setUpdatedDate(Util1.getTodayLocalDateTime());
        }
        formulaDao.save(s);
        return s;
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockFormula", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        return formulaDao.delete(key);
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        return formulaDao.getFormula(compCode);
    }

    @Override
    public StockFormulaPrice save(StockFormulaPrice s) {
        return formulaPriceDao.save(s);
    }

    @Override
    public StockFormulaQty save(StockFormulaQty s) {
        return formulaQtyDao.save(s);
    }

    @Override
    public GradeDetail save(GradeDetail s) {
        return gradeDetailDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaPriceKey key) {
        return formulaPriceDao.delete(key);
    }

    @Override
    public boolean delete(GradeDetailKey key) {
        return gradeDetailDao.delete(key);
    }

    @Override
    public String getMaxDate() {
        return formulaDao.getMaxDate();
    }

    @Override
    public String getMaxDateSFPrice() {
        return formulaPriceDao.getMaxDate();
    }

    @Override
    public String getMaxDateSFQty() {
        return formulaQtyDao.getMaxDate();
    }

    @Override
    public String getMaxDateGD() {
        return gradeDetailDao.getMaxDate();
    }

    @Override
    public StockFormula find(StockFormulaKey key) {
        return formulaDao.find(key);
    }

    @Override
    public List<StockFormulaPrice> getStockFormulaPrice(String formulaCode, String compCode) {
        return formulaPriceDao.getStockFormulaPrice(formulaCode, compCode);
    }

    @Override
    public List<StockFormulaQty> getStockFormulaQty(String formulaCode, String compCode) {
        return formulaQtyDao.getStockFormulaQty(formulaCode, compCode);
    }

    @Override
    public List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode) {
        return gradeDetailDao.getGradeDetail(formulaCode, criteriaCode, compCode);
    }
}
