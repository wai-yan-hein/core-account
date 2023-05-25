/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@Embeddable
public class ExchangeKey implements Serializable{

    @Column(name = "ex_code")
    private String exCode;
    @Column(name = "comp_code")
    private String compCode;
}
