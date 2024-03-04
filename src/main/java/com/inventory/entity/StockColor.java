package com.inventory.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockColor {
    private String colorId;
    private String colorName;
    private String compCode;
    private LocalDateTime updatedDate;
}
