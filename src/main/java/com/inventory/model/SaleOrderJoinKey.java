package com.inventory.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class SaleOrderJoinKey implements Serializable {
    private String saleVouNo;
    private String orderVouNo;
}
