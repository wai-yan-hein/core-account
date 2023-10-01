package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Data
@Embeddable
public class DateLockKey implements Serializable {

    @Column(name = "lock_code")
    private String lockCode;
    @Column(name = "comp_code")
    private String compCode;
}
