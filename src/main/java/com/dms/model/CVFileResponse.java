/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dms.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class CVFileResponse {

    private String statusCodeValue;
    private String statusCode;
    private CVFile body;
}
