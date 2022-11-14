/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.user.model.Currency;
import com.acc.model.Department;
import com.acc.model.DepartmentKey;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.acc.model.TraderA;
import com.acc.model.VDescription;
import com.acc.model.VRef;
import com.acc.model.VTranSource;
import com.common.Global;
import com.common.Util1;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class AccountRepo {

    int min = 1;

    @Autowired
    private WebClient accountApi;

    public Department getDefaultDepartment() {
        String deptCode = Global.hmRoleProperty.get("default.department");
        return findDepartment(Util1.isNull(deptCode, "-"));
    }

    public Department findDepartment(String deptCode) {
        DepartmentKey key = new DepartmentKey();
        key.setDeptCode(Util1.isNull(deptCode, "-"));
        key.setCompCode(Global.compCode);
        Mono<Department> result = accountApi.post()
                .uri("/account/find-department")
                .body(Mono.just(key), DepartmentKey.class)
                .retrieve()
                .bodyToMono(Department.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Currency findCurrency(String curCode) {
        Mono<ResponseEntity<Currency>> result = accountApi.get()
                .uri(builder -> builder.path("/account/find-currency")
                .queryParam("curCode", curCode)
                .build())
                .retrieve().toEntity(Currency.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Department> getDepartment() {
        Mono<ResponseEntity<List<Department>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-department")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    return Mono.error(new IllegalStateException(response.statusCode().getReasonPhrase()));
                }).toEntityList(Department.class);
        return result.block().getBody();
    }

    public List<TraderA> getTrader() {
        Mono<ResponseEntity<List<TraderA>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-trader")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(TraderA.class);
        return result.block().getBody();
    }

    public List<ChartOfAccount> getChartOfAccount() {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block().getBody();
    }

    public List<ChartOfAccount> getTraderAccount() {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-trader-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block().getBody();
    }

    public List<ChartOfAccount> getCOA(String str) {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-coa-lv3")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block().getBody();
    }

    public List<Currency> getCurrency() {
        Mono<ResponseEntity<List<Currency>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-currency")
                .build())
                .retrieve().toEntityList(Currency.class);
        return result.block().getBody();
    }

    public List<VRef> getReference(String str) {
        Mono<ResponseEntity<List<VRef>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-reference")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve().toEntityList(VRef.class);
        return result.block().getBody();
    }

    public List<VDescription> getDescription(String str) {
        Mono<ResponseEntity<List<VDescription>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-description")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve().toEntityList(VDescription.class);
        return result.block().getBody();
    }

    public List<VTranSource> getTranSource() {
        Mono<ResponseEntity<List<VTranSource>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-tran-source")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(VTranSource.class);
        return result.block().getBody();
    }

    public Gl saveGl(Gl gl) {
        Mono<Gl> result = accountApi.post()
                .uri("/account/save-gl")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(Gl.class);
        return result.block(Duration.ofMinutes(min));
    }

    public boolean deleteGl(GlKey gl) {
        Mono<Boolean> result = accountApi.post()
                .uri("/account/delete-gl")
                .body(Mono.just(gl), GlKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
        return result.block(Duration.ofMinutes(min));
    }

    public ChartOfAccount saveCOA(ChartOfAccount coa) {
        Mono<ChartOfAccount> result = accountApi.post()
                .uri("/account/save-coa")
                .body(Mono.just(coa), ChartOfAccount.class)
                .retrieve()
                .bodyToMono(ChartOfAccount.class);
        return result.block(Duration.ofMinutes(min));
    }

    public double getTraderBalance(String date, String traderCode, String compCode) {
        Mono<ResponseEntity<Double>> result = accountApi.get()
                .uri(builder -> builder.path("/account/report/get-trader-balance")
                .queryParam("date", date)
                .queryParam("traderCode", traderCode)
                .queryParam("compCode", compCode)
                .build())
                .retrieve().toEntity(Double.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<ChartOfAccount> getCOAChild(String coaCode) {
        COAKey key = new COAKey();
        key.setCoaCode(coaCode);
        key.setCompCode(Global.compCode);
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.post()
                .uri("/account/get-coa-child")
                .body(Mono.just(key), COAKey.class)
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public ChartOfAccount findCOA(String coaCode) {
        COAKey key = new COAKey();
        key.setCoaCode(coaCode);
        key.setCompCode(Global.compCode);
        Mono<ResponseEntity<ChartOfAccount>> result = accountApi.post()
                .uri("/account/find-coa")
                .body(Mono.just(key), COAKey.class)
                .retrieve().toEntity(ChartOfAccount.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Department saveDepartment(Department dep) {
        Mono<Department> result = accountApi.post()
                .uri("/account/save-department")
                .body(Mono.just(dep), Department.class)
                .retrieve()
                .bodyToMono(Department.class);
        return result.block();
    }

    public List<Department> getDepartmentTree() {
        Mono<ResponseEntity<List<Department>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-department-tree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Department.class);
        return result.block().getBody();
    }
}
