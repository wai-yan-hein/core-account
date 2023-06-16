/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.acc.model.GlLog;
import com.acc.model.GlLogKey;
import com.common.ReturnObject;
import com.common.Util1;
import com.h2.dao.GlDao;
import com.h2.dao.GlLogDao;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Slf4j
@Transactional
public class GlServiceImpl implements GlService {

    @Autowired
    private GlDao dao;

    @Autowired
    private SeqService seqService;

    @Autowired
    private GlLogDao logDao;

    @Override
    public ReturnObject save(List<Gl> glList) {
        ReturnObject ro = new ReturnObject();
        if (!glList.isEmpty()) {
            Gl tmp = glList.get(0);
            String vouNo = tmp.getRefNo();
            String tranSource = tmp.getTranSource();
            String compCode = tmp.getKey().getCompCode();
            LocalDateTime glDate = tmp.getGlDate();
            boolean delete = tmp.isDeleted();
            String glVouNo = tmp.getGlVouNo();
            switch (tranSource) {
                case "GV", "DR", "CR" -> {
                    if (Util1.isNullOrEmpty(glVouNo)) {
                        glVouNo = getVouNo(glDate, tmp.getKey().getDeptId(), tmp.getMacId(), tmp.getKey().getCompCode(), tranSource);
                    }
                }
                default ->
                    dao.deleteInvVoucher(vouNo, tranSource, compCode);
            }
            if (!delete) {
                for (Gl gl : glList) {
                    //convert to uni code
                    gl.setDescription(Util1.convertToUniCode(gl.getDescription()));
                    gl.setReference(Util1.convertToUniCode(gl.getReference()));
                    if (gl.getSrcAccCode() != null) {
                        if (Util1.isMultiCur()) {
                            if (gl.isCash()) {
                                gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                            }
                        }
                        double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                        if (amt > 0) {
                            gl.setGlVouNo(glVouNo);
                            save(gl, false);

                        }
                    }
                }
            }
            ro.setGlVouNo(glVouNo);
            ro.setVouNo(vouNo);
            ro.setTranSource(tranSource);
            ro.setCompCode(compCode);
        }
        return ro;
    }

    @Override
    public Gl save(Gl gl, boolean backup) {
        String updatedBy = gl.getModifyBy();
        gl.setGlDate(Util1.toDateTime(gl.getGlDate()));
        if (Util1.isNull(gl.getKey().getGlCode())) {
            gl.setCreatedDate(LocalDateTime.now());
            Integer macId = gl.getMacId();
            String compCode = gl.getKey().getCompCode();
            String glCode = getGLCode(gl.getGlDate(), gl.getKey().getDeptId(), macId, compCode);
            GlKey key = gl.getKey();
            key.setGlCode(glCode);
            Gl valid = findByCode(key);
            if (Objects.isNull(valid)) {
                gl.getKey().setGlCode(glCode);
            } else {
                log.info(valid.getKey().getGlCode());
                throw new IllegalStateException("Duplication Occur in Gl");
            }
        }
        if (gl.getDelList() != null) {
            for (GlKey key : gl.getDelList()) {
                dao.delete(key, updatedBy);
            }
        }
        //glProcessor.process(gl);
        return dao.save(gl);
    }

    @Override
    public List<Gl> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public Gl findById(GlKey key) {
        return dao.findById(key);
    }

    @Override
    public Gl findByCode(GlKey key) {
        return dao.findByCode(key);
    }

    private String getGLCode(LocalDateTime date, Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(date, "ddMMyy");
        int seqNo = seqService.getSequence(macId, "GL", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + String.format("%0" + 5 + "d", seqNo);
    }

    private String getVouNo(LocalDateTime glDate, Integer deptId, Integer macId, String compCode, String type) {
        String period = Util1.toDateStr(glDate, "MMyy");
        int seqNo = seqService.getSequence(macId, type, period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId);
        return type + deptCode + String.format("%0" + 2 + "d", macId) + "-" + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public List<Gl> unUploadVoucher(String compCode) {
        return dao.unUploadVoucher(compCode);
    }

    @Override
    public Gl updateACK(GlKey key) {
        return dao.updateACK(key);
    }

}
