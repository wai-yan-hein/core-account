/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Athu Sint
 */
@Data
@Entity(name = "language")
public class Language {

    @EmbeddedId
    private LanguageKey key;
    @Column(name = "lan_value")
    private String lanValue;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
