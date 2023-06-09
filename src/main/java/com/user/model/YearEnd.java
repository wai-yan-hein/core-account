/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import java.util.Date;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YearEnd {
    private String yeCompCode;
    private String compCode;
    private Date startDate;
    private Date endDate;
    private Date yearEndDate;
    private boolean batchLock;
    private boolean opening;
    private String createBy;
    private Date cratedDate;
    private String message;
}
