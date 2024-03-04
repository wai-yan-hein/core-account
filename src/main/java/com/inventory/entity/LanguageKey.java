/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Athu Sint
 */
@Data
@Embeddable
public class LanguageKey implements Serializable{
    @Column(name = "lan_tupe")
    private String lanType;
    @Column(name = "lan_key")
    private String lanKey;
}
