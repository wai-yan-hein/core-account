/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YearEnd {
    private String yeCompCode;
    private String compCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate yearEndDate;
    private boolean batchLock;
    private boolean opening;
    private String createBy;
    private LocalDateTime createdDate;
    private String message;
    private String token;
}
