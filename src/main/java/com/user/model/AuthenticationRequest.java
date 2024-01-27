/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Builder
public class AuthenticationRequest {

    private String serialNo;
    private String password;
    

}
