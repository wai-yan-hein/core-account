/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "miling_expense")
public class MillingExpense {

    @EmbeddedId
    private MillingExpenseKey key;
    @Column(name = "expense_name")
    private String expenseName;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @Column(name = "amount")
    private Float amount;
}
