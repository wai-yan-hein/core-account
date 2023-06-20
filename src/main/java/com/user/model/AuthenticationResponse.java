/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private long accessTokenExpired;
    private long refreshTokenExpired;
}
