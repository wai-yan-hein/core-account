package com.user.model;


import lombok.Data;

@Data
public class SyncModel {
    private String tranSource;
    private String fromDate;
    private String toDate;
    private boolean sync;
    private boolean ack;
}
