/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuTemplateKey {

    private Integer menuId;
    private Integer busId;
}
