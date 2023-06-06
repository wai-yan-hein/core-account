/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.service;

import com.common.Util1;
import com.h2.dao.OrderHisDao;
import com.h2.dao.OrderHisDetailDao;
import com.h2.dao.SeqDao;
import com.inventory.model.General;
import com.inventory.model.OrderDetailKey;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisDetail;
import com.inventory.model.OrderHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
public class OrderHisServiceImpl implements OrderHisService {

    @Autowired
    private OrderHisDao shDao;
    @Autowired
    private OrderHisDetailDao sdDao;
    @Autowired
    private SeqDao seqDao;

    @Override
    public OrderHis save(OrderHis orderHis) {
        orderHis.setVouDate(Util1.toDateTime(orderHis.getVouDate()));
        if (Util1.isNullOrEmpty(orderHis.getKey().getVouNo())) {
            orderHis.getKey().setVouNo(getVoucherNo(orderHis.getKey().getDeptId(), orderHis.getMacId(), orderHis.getKey().getCompCode()));
        }
        List<OrderHisDetail> listSD = orderHis.getListSH();
        List<OrderDetailKey> listDel = orderHis.getListDel();
        String vouNo = orderHis.getKey().getVouNo();
        //backup
        if (listDel != null) {
            listDel.forEach(key -> sdDao.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            OrderHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                OrderDetailKey key = new OrderDetailKey();
                key.setDeptId(orderHis.getKey().getDeptId());
                key.setCompCode(orderHis.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        OrderHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                sdDao.save(cSd);
            }
        }
        shDao.save(orderHis);
        orderHis.setListSH(listSD);
        return orderHis;
    }

    @Override
    public OrderHis update(OrderHis orderHis) {
        return shDao.save(orderHis);
    }

    @Override
    public List<OrderHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return shDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public OrderHis findById(OrderHisKey id) {
        return shDao.findById(id);
    }

    @Override
    public void delete(OrderHisKey key) throws Exception {
        shDao.delete(key);
    }

    @Override
    public void restore(OrderHisKey key) throws Exception {
        shDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<OrderHis> unUploadVoucher(String syncDate) {
        return shDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<OrderHis> unUpload(String syncDate) {
        return shDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return shDao.getMaxDate();
    }

    @Override
    public List<OrderHis> search(String updatedDate, List<String> location) {
        return shDao.search(updatedDate, location);
    }

    @Override
    public void truncate(OrderHisKey key) {
        shDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return shDao.getVoucherInfo(vouDate, compCode, depId);
    }

    @Override
    public OrderHis updateACK(OrderHisKey key) {
        return shDao.updateACK(key);
    }

}
