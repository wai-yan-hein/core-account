/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.Util1;
import com.h2.dao.SeqDao;
import com.h2.dao.StockInOutDao;
import com.h2.dao.StockInOutDetailDao;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockInOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.annotation.Lazy;

/**
 * @author wai yan
 */
@Lazy
@Service
@Transactional
@Slf4j
public class StockInOutServiceImpl implements StockInOutService {

    @Autowired
    private StockInOutDao ioDao;
    @Autowired
    private StockInOutDetailDao iodDao;
    @Autowired
    private SeqDao seqDao;

    @Override
    public StockInOut save(StockInOut io) {
        io.setVouDate(Util1.toDateTime(io.getVouDate()));
        if (Util1.isNullOrEmpty(io.getKey().getVouNo())) {
            io.getKey().setVouNo(getVoucherNo(io.getDeptId(), io.getMacId(), io.getKey().getCompCode()));
        }

        List<StockInOutDetail> listSD = io.getListSH();
        List<StockInOutKey> listDel = io.getListDel();
        String vouNo = io.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> iodDao.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            StockInOutDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                StockInOutKey key = new StockInOutKey();
                key.setCompCode(io.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
                cSd.setDeptId(io.getDeptId());
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        StockInOutDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                iodDao.save(cSd);
            }
        }
        ioDao.save(io);
        io.setListSH(listSD);
        return io;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp, String vouNo, String userCode, String vouStatus) {
        return ioDao.search(fromDate, toDate, remark, desp, vouNo, userCode, vouStatus);
    }

    @Override
    public StockInOut findById(StockIOKey id) {
        return ioDao.findById(id);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "STOCKIO", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

}
