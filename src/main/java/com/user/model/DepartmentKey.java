package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class DepartmentKey  implements Serializable{
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "comp_code")
    private String compCode;
}
