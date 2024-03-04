package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Entity(name = "country")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class Country {

    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "country_name")
    private String countryName;

    public Country() {
    }

    public Country(String id, String countryName) {
        this.code = id;
        this.countryName = countryName;
    }
}
