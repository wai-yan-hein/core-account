/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.Currency;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface CurrencyService {

    Currency save(Currency cur);

    String getMaxDate();

    List<Currency> findAll();

    Currency findById(String curCode);
}
