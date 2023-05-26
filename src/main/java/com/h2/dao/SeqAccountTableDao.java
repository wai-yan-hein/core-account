/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2.dao;
import com.acc.model.SeqKeyAccount;
import com.acc.model.SeqAccountTable;
import java.util.List;

/**
 *
 * @author winswe
 */
 public interface SeqAccountTableDao {

     SeqAccountTable save(SeqAccountTable st);

     SeqAccountTable findById(SeqKeyAccount id);

     List<SeqAccountTable> search(String option, String period, String compCode);

     SeqAccountTable getSeqTable(String option, String period, String compCode);

     int delete(Integer id);

     int getSequence(Integer macId,String option, String period, String compCode);

     List<SeqAccountTable> findAll(String compCode);
}
