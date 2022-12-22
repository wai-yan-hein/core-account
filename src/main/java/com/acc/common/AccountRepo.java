/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.common;

import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.acc.model.DeleteObj;
import com.user.model.Currency;
import com.acc.model.Department;
import com.acc.model.DepartmentKey;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.acc.model.OpeningBalance;
import com.acc.model.ReportFilter;
import com.acc.model.StockOP;
import com.acc.model.TmpOpening;
import com.acc.model.TraderA;
import com.acc.model.VDescription;
import com.acc.model.VRef;
import com.acc.report.StockOPKey;
import com.common.FilterObject;
import com.common.Global;
import com.common.ReturnObject;
import com.common.Util1;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
                .toEntityList(Department.class);
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

    public List<TraderA> getTrader(String text) {
        Mono<ResponseEntity<List<TraderA>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/search-trader")
                .queryParam("compCode", Global.compCode)
                .queryParam("text", text)
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

    public List<Gl> getTranSource() {
        Mono<ResponseEntity<List<Gl>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-tran-source")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Gl.class);
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

    public ReturnObject saveGl(List<Gl> gl) {
        Mono<ReturnObject> result = accountApi.post()
                .uri("/account/save-gl-list")
                .body(Mono.just(gl), List.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        return result.block(Duration.ofMinutes(min));
    }

    public boolean delete(DeleteObj obj) {
        Mono<Boolean> result = accountApi.post()
                .uri("/account/delete-gl")
                .body(Mono.just(obj), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
        return result.block(Duration.ofMinutes(min));
    }

    public boolean deleteJouranl(DeleteObj gl) {
        Mono<Boolean> result = accountApi.post()
                .uri("/account/delete-journal")
                .body(Mono.just(gl), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
        return result.block(Duration.ofMinutes(min));
    }

    public boolean delete(StockOPKey key) {
        Mono<Boolean> result = accountApi.post()
                .uri("/account/delete-stock-op")
                .body(Mono.just(key), StockOPKey.class)
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
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-coa-child")
                .queryParam("coaCode", coaCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block().getBody();
    }

    public List<ChartOfAccount> getCOA(String str, Integer level) {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/search-coa")
                .queryParam("str", str)
                .queryParam("level", level)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);
        return result.block().getBody();
    }

    public List<Gl> getJournal(String vouNo) {
        Mono<ResponseEntity<List<Gl>>> result = accountApi.get()
                .uri(builder -> builder.path("/account/get-journal")
                .queryParam("glVouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Gl.class);
        return result.block().getBody();
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

    public List<OpeningBalance> getCOAOpening(ReportFilter filter) {
        Mono<ResponseEntity<List<OpeningBalance>>> result = accountApi
                .post()
                .uri("/report/get-coa-opening")
                .body(Mono.just(filter), FilterObject.class
                ).retrieve().toEntityList(OpeningBalance.class);
        return result.block().getBody();
    }

    public OpeningBalance saveCOAOpening(OpeningBalance opening) {
        Mono<OpeningBalance> result = accountApi.post()
                .uri("/account/save-opening")
                .body(Mono.just(opening), OpeningBalance.class)
                .retrieve()
                .bodyToMono(OpeningBalance.class);
        return result.block();
    }

    public List<TmpOpening> getOpening(ReportFilter filter) {
        Mono<ResponseEntity<List<TmpOpening>>> result = accountApi.post()
                .uri("/account/get-coa-opening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(TmpOpening.class);
        return result.block().getBody();
    }

    public StockOP save(StockOP op) {
        Mono<StockOP> result = accountApi.post()
                .uri("/account/save-stock-op")
                .body(Mono.just(op), StockOP.class)
                .retrieve()
                .bodyToMono(StockOP.class);
        return result.block();
    }

}
