/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;
import com.acc.model.SeqKeyAccount;
import com.acc.model.SeqAccountTable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author winswe
 */
@Repository
public class SeqAccountTableDaoImpl extends AbstractDao<SeqKeyAccount, SeqAccountTable> implements SeqAccountTableDao {

    @Override
    public SeqAccountTable save(SeqAccountTable st) {
        persist(st);
        return st;
    }

    @Override
    public SeqAccountTable findById(SeqKeyAccount id) {
        return getByKey(id);
    }

    @Override
    public List<SeqAccountTable> search(String option, String period, String compCode) {
        String strSql = "select o from SeqTable o where o.seqOption = '" + option
                + "' and o.compCode = '" + compCode + "'";

        if (!period.equals("-")) {
            strSql = strSql + " and o.period = '" + period + "'";
        }

        return findHSQL(strSql);
    }

    @Override
    public SeqAccountTable getSeqTable(String option, String period, String compCode) {
        List<SeqAccountTable> listST = search(option, period, compCode);
        SeqAccountTable st = null;

        if (listST != null) {
            if (!listST.isEmpty()) {
                st = listST.get(0);
            }
        }

        return st;
    }

    @Override
    public int delete(Integer id) {
        String sql = "delete from SeqTable o where o.id = " + id;
        execSql(sql);
        return 1;
    }

    @Override
    public int getSequence(Integer macId, String option, String period, String compCode) {
        SeqKeyAccount key = new SeqKeyAccount();
        key.setCompCode(compCode);
        key.setMacId(macId);
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqAccountTable st = findById(key);
        if (st == null) {
            st = new SeqAccountTable();
            st.setKey(key);
            st.setSeqNo(1);
        } else {
            st.setSeqNo(st.getSeqNo() + 1);
        }
        save(st);
        return st.getSeqNo();
    }

    @Override
    public List<SeqAccountTable> findAll(String compCode) {
        String strSql = "select o from SeqTable o where o.key.compCode ='" + compCode + "'";
        return findHSQL(strSql);
    }
}
