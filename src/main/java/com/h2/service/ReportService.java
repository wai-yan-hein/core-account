/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.acc.model.Gl;
import com.inventory.model.General;
import com.inventory.model.VPurchase;
import com.inventory.model.VReturnIn;
import com.inventory.model.VReturnOut;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface ReportService {

    General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId);

    General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId);

    List<Gl> getIndividualLedger(String fromDate, String toDate, String desp, String srcAcc,
            String acc, String curCode, String reference,
            String compCode, String tranSource, String traderCode, String traderType,
            String coaLv2, String coaLv1, String batchNo, String projectNo,
            boolean summary, Integer macId);

    List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo,
            String userCode, String remark, String locCode, String compCode,
            Integer deptId, String deleted, String projectNo, String curCode);

    List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode, String vouNo,
            String userCode, String remark, String locCode, String compCode,
            Integer deptId, String deleted, String projectNo, String curCode);

    List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode, String vouNo,
            String userCode, String remark, String locCode, String compCode,
            Integer deptId, String deleted, String projectNo, String curCode);
}
