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
import com.inventory.model.LocationKey;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockInOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
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
            io.getKey().setVouNo(getVoucherNo(io.getKey().getDeptId(), io.getMacId(), io.getKey().getCompCode()));
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
                key.setDeptId(io.getKey().getDeptId());
                key.setCompCode(io.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
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

    @Override
    public void delete(StockIOKey key) throws Exception {
        ioDao.delete(key);
    }

    @Override
    public void restore(StockIOKey key) throws Exception {
        ioDao.restore(key);
    }

    @Override
    public List<StockInOut> unUpload(String syncDate) {
        return ioDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return ioDao.getMaxDate();
    }

    @Override
    public List<StockInOut> search(String updatedDate, List<LocationKey> keys) {
        return ioDao.search(updatedDate, keys);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "STOCKIO", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

}
