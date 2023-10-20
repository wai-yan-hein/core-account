package com.h2.service;

import com.common.Util1;
import com.h2.dao.GradeDetailDao;
import com.h2.dao.StockFormulaDao;
import com.h2.dao.StockFormulaDetailDao;
import com.h2.dao.StockFormulaQtyDao;
import com.inventory.model.GradeDetailKey;
import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaPrice;
import com.inventory.model.StockFormulaPriceKey;
import com.inventory.model.StockFormulaKey;
import com.inventory.model.StockFormulaQty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {

    private final StockFormulaDao formulaDao;
    private final StockFormulaDetailDao formulaDetailDao;
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
        return formulaDetailDao.save(s);
    }

    @Override
    public StockFormulaQty save(StockFormulaQty s) {
        return formulaQtyDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaPriceKey key) {
        return formulaDetailDao.delete(key);
    }

    @Override
    public boolean delete(GradeDetailKey key) {
        return gradeDetailDao.delete(key);
    }

    @Override
    public List<StockFormulaPrice> getFormulaDetail(String code, String compCode) {
        return formulaDetailDao.getFormulaDetail(code, compCode);
    }

    @Override
    public String getMaxDate() {
        return formulaDao.getMaxDate();
    }

    @Override
    public String getMaxDateSFPrice() {
        return formulaDetailDao.getMaxDate();
    }

    @Override
    public String getMaxDateSFQty() {
        return formulaQtyDao.getMaxDate();
    }

    @Override
    public StockFormula find(StockFormulaKey key) {
        return formulaDao.find(key);
    }
}
