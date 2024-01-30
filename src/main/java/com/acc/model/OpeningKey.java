/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author myoht
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpeningKey {

    private String coaOpId;
    private String compCode;
}
