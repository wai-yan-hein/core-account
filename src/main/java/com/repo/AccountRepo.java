/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.H2Repo;
import com.acc.model.COAKey;
import com.acc.model.COATemplate;
import com.acc.model.COATemplateKey;
import com.acc.model.ChartOfAccount;
import com.acc.model.DateModel;
import com.acc.model.DeleteObj;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.acc.model.Gl;
import com.acc.model.OpeningBalance;
import com.acc.model.OpeningKey;
import com.acc.model.StockOP;
import com.acc.model.TmpOpening;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.acc.model.StockOPKey;
import com.acc.model.VApar;
import com.acc.model.VDescription;
import com.acc.model.VTriBalance;
import com.common.Global;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.ReturnObject;
import com.common.Util1;
import com.inventory.model.Message;
import com.inventory.model.MessageType;
import com.inventory.model.TraderGroup;
import com.inventory.model.TraderGroupKey;
import com.model.VoucherInfo;
import com.user.model.YearEnd;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class AccountRepo {

    @Autowired
    private WebClient accountApi;
    @Autowired
    private WebClient accountApiSecond;
    @Autowired
    private boolean localDatabase;
    @Autowired
    private H2Repo h2Repo;

    public Mono<DepartmentA> getDefaultDepartment() {
        String deptCode = Global.hmRoleProperty.get("default.department");
        String depByUser = Global.loginUser.getDeptCode();
        return findDepartment(Util1.isNull(depByUser, deptCode));
    }

    public Mono<ChartOfAccount> getDefaultCash() {
        String coaCode = Global.hmRoleProperty.get("default.cash");
        return findCOA(Util1.isNull(coaCode, "-"));
    }

    public Mono<DepartmentA> findDepartment(String deptCode) {
        if (Util1.isNullOrEmpty(deptCode)) {
            return Mono.empty();
        }
        DepartmentAKey key = new DepartmentAKey();
        key.setDeptCode(deptCode);
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return accountApi.post()
                .uri("/account/findDepartment")
                .body(Mono.just(key), DepartmentAKey.class)
                .retrieve()
                .bodyToMono(DepartmentA.class)
                .onErrorResume((e) -> {
                    log.error("findDepartment : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<DepartmentA>> getDepartment() {
        if (localDatabase) {
            return h2Repo.getDepartmentAccount();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getDepartment")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(DepartmentA.class)
                .onErrorResume((e) -> {
                    log.error("getDepartment : " + e.getMessage());
                    return Mono.empty();
                }).collectList();
    }

    public Mono<List<TraderA>> getTrader() {
        if (localDatabase) {
            return h2Repo.getTraderAccount();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getTrader")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(TraderA.class)
                .collectList();
    }

    public Mono<List<TraderA>> getTrader(String text) {
        if (localDatabase) {
            return h2Repo.getTrader(text);
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/searchTrader")
                .queryParam("compCode", Global.compCode)
                .queryParam("text", text)
                .build())
                .retrieve()
                .bodyToFlux(TraderA.class)
                .collectList();
    }

    public Mono<List<ChartOfAccount>> getChartOfAccount(Integer coaLevel) {
        if (localDatabase) {
            return h2Repo.getChartofAccount().collectList();
        }
        return accountApiSecond.get()
                .uri(builder -> builder.path("/account/getCoa")
                .queryParam("coaLevel", coaLevel)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .collectList();
    }

    public Mono<List<ChartOfAccount>> getCOATree() {
        if (localDatabase) {
            return h2Repo.getCOATree();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOATree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .collectList();
    }

    public Mono<List<COATemplate>> getCOATemplateTree(Integer busId) {
        return accountApi.get()
                .uri(builder -> builder.path("/template/getCOATemplateTree")
                .queryParam("busId", busId)
                .build())
                .retrieve()
                .bodyToFlux(COATemplate.class)
                .collectList();
    }

    public Mono<List<COATemplate>> getCOATemplate(Integer busId) {
        return accountApi.get()
                .uri(builder -> builder.path("/template/getCOATemplate")
                .queryParam("busId", busId)
                .build())
                .retrieve()
                .bodyToFlux(COATemplate.class)
                .collectList();
    }

    public Flux<ChartOfAccount> getTraderAccount() {
        if (localDatabase) {
            return h2Repo.getTraderCOA();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getTraderCOA")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class);
    }

    public Mono<List<Gl>> getTranSource() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getTranSource")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Mono<Gl> save(Gl gl) {
        return accountApi.post()
                .uri("/account/saveGl")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(Gl.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<Gl> uploadGL(Gl gl) {
        return accountApi.post()
                .uri("/account/saveGl")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(Gl.class);
    }

    public Mono<ReturnObject> saveGl(List<Gl> gl) {
        return accountApi.post()
                .uri("/account/saveGlList")
                .body(Mono.just(gl), List.class)
                .retrieve()
                .bodyToMono(ReturnObject.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<Boolean> delete(COATemplateKey obj) {
        return accountApi.post()
                .uri("/template/deleteCOATemplate")
                .body(Mono.just(obj), COATemplateKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(DepartmentAKey key) {
        return accountApi.post()
                .uri("/account/deleteDepartment")
                .body(Mono.just(key), COATemplateKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(DeleteObj obj) {
        return accountApi.post()
                .uri("/account/deleteGl")
                .body(Mono.just(obj), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(OpeningKey key) {
        return accountApi.post()
                .uri("/account/deleteOP")
                .body(Mono.just(key), DeleteObj.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(COAKey key) {
        return accountApi.post()
                .uri("/account/deleteCOA")
                .body(Mono.just(key), COAKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> deleteVoucher(DeleteObj gl) {
        return accountApi.post()
                .uri("/account/deleteVoucher")
                .body(Mono.just(gl), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(StockOPKey key) {
        return accountApi.post()
                .uri("/account/deleteStockOP")
                .body(Mono.just(key), StockOPKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<COATemplate> save(COATemplate coa) {
        return accountApi.post()
                .uri("/template/saveCOA")
                .body(Mono.just(coa), COATemplate.class)
                .retrieve()
                .bodyToMono(COATemplate.class);
    }

    public Mono<ChartOfAccount> saveCOA(ChartOfAccount coa) {
        return accountApi.post()
                .uri("/account/saveCOA")
                .body(Mono.just(coa), ChartOfAccount.class)
                .retrieve()
                .bodyToMono(ChartOfAccount.class)
                .onErrorResume((e) -> {
                    log.error(e.getMessage());
                    return Mono.error(e);
                })
                .doOnSuccess(
                        (t) -> {
                            if (localDatabase) {
                                h2Repo.save(t);
                            }
                        }
                );
    }

    public Mono<List<ChartOfAccount>> getUpdateChartOfAccountByDate(String updatedDate) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getUpdatedCOA")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .collectList();
    }

    public Mono<List<TraderA>> getUpdateTraderByDate(String updatedDate) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getUpdatedTrader")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(TraderA.class)
                .collectList();
    }

    public Mono<List<DepartmentA>> getUpdateDepartmentAByDate(String updatedDate) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getUpdatedDepartment")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(DepartmentA.class)
                .collectList();
    }

    public Double getTraderBalance(String date, String traderCode, String curCode, String compCode) {
        try {
            return accountApi.get()
                    .uri(builder -> builder.path("/report/getTraderBalance")
                    .queryParam("date", date)
                    .queryParam("traderCode", traderCode)
                    .queryParam("curCode", curCode)
                    .queryParam("compCode", compCode)
                    .build())
                    .retrieve()
                    .bodyToMono(Double.class).block();
        } catch (Exception e) {
            log.error("getTraderBalance : " + e.getMessage());
        }
        return 0.0;
    }

    public Mono<List<COATemplate>> getCOAChildTemplate(String coaCode, Integer busId) {
        return accountApi.get()
                .uri(builder -> builder.path("/template/getCOAChild")
                .queryParam("coaCode", coaCode)
                .queryParam("busId", busId)
                .build())
                .retrieve()
                .bodyToFlux(COATemplate.class)
                .collectList();
    }

    public Flux<ChartOfAccount> getCOAChild(String coaCode) {
        if (localDatabase) {
            return h2Repo.getCOAChild(coaCode);

        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOAChild")
                .queryParam("coaCode", coaCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class);
    }

    public Mono<List<ChartOfAccount>> getCOAByGroup(String groupCode) {
        if (localDatabase) {
            return h2Repo.getCOAByGroup(groupCode);

        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOAByGroup")
                .queryParam("groupCode", groupCode == null ? "-" : groupCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .onErrorResume((e) -> {
                    log.error("getCOAByGroup : " + e.getMessage());
                    return Mono.empty();
                }).collectList();
    }

    public Mono<List<ChartOfAccount>> getCashBank() {
        Mono<List<ChartOfAccount>> m1 = getCOAByGroup(ProUtil.getProperty(ProUtil.CASH_GROUP));
        Mono<List<ChartOfAccount>> m2 = getCOAByGroup(ProUtil.getProperty(ProUtil.BANK_GROUP));
        return Mono.zip(m1, m2)
                .map(tuple -> {
                    List<ChartOfAccount> combinedList = new ArrayList<>();
                    combinedList.addAll(tuple.getT1());
                    combinedList.addAll(tuple.getT2());
                    return combinedList;
                });
    }

    public Mono<List<ChartOfAccount>> getPurchaseAcc() {
        return getCOAByHead(ProUtil.getProperty(ProUtil.PURCHASE));
    }

    public Mono<List<ChartOfAccount>> getPayableAcc() {
        return getCOAByHead(ProUtil.getProperty(ProUtil.LIA));
    }

    public Mono<List<ChartOfAccount>> getCOAByHead(String headCode) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOAByHead")
                .queryParam("headCode", headCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .onErrorResume((e) -> {
                    log.error("getCOAByHead : " + e.getMessage());
                    return Mono.empty();
                })
                .collectList();
    }

    public Mono<List<Gl>> getJournal(String vouNo) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getJournal")
                .queryParam("glVouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Mono<List<Gl>> getVoucher(String vouNo) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getVoucher")
                .queryParam("glVouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Mono<COATemplate> findCOATemplate(COATemplateKey key) {
        return accountApi.post()
                .uri("/template/findCOATemplate")
                .body(Mono.just(key), COATemplateKey.class)
                .retrieve()
                .bodyToMono(COATemplate.class);
    }

    public Mono<ChartOfAccount> findCOA(String coaCode) {
        if (Util1.isNullOrEmpty(coaCode)) {
            return Mono.empty();
        }
        COAKey key = new COAKey();
        key.setCoaCode(coaCode);
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);

        }
        return accountApi.post()
                .uri("/account/findCOA")
                .body(Mono.just(key), COAKey.class)
                .retrieve()
                .bodyToMono(ChartOfAccount.class)
                .onErrorResume((e) -> {
                    log.error("findCOA :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<DepartmentA> saveDepartment(DepartmentA dep) {
        return accountApi.post()
                .uri("/account/saveDepartment")
                .body(Mono.just(dep), DepartmentA.class)
                .retrieve()
                .bodyToMono(DepartmentA.class)
                .onErrorResume((e) -> {
                    log.error("saveDepartment : " + e.getMessage());
                    return Mono.empty();
                })
                .doOnSuccess((t) -> {
                    h2Repo.save(t);
                });
    }

    public Flux<DepartmentA> getDepartmentTree() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getDepartmentTree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(DepartmentA.class);
    }

    public Mono<OpeningBalance> saveCOAOpening(OpeningBalance opening) {
        return accountApi.post()
                .uri("/account/saveOpening")
                .body(Mono.just(opening), OpeningBalance.class)
                .retrieve()
                .bodyToMono(OpeningBalance.class);
    }

    public Mono<TmpOpening> getOpening(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/getCOAOpening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToMono(TmpOpening.class)
                .onErrorResume((e) -> {
                    log.info("getOpening " + e.getMessage());
                    return Mono.empty();
                }).defaultIfEmpty(new TmpOpening());
    }

    public Mono<StockOP> save(StockOP op) {
        return accountApi.post()
                .uri("/account/saveStockOP")
                .body(Mono.just(op), StockOP.class)
                .retrieve()
                .bodyToMono(StockOP.class);
    }

    public Mono<TraderA> saveTrader(TraderA t) {
        return accountApi.post()
                .uri("/account/saveTrader")
                .body(Mono.just(t), TraderA.class)
                .retrieve()
                .bodyToMono(TraderA.class)
                .doOnSuccess((trader) -> {
                    h2Repo.save(trader);
                });
    }

    public Mono<TraderA> findTrader(String traderCode) {
        TraderAKey key = new TraderAKey();
        key.setCode(traderCode);
        key.setCompCode(Global.compCode);
        return accountApi.post()
                .uri("/account/findTrader")
                .body(Mono.just(key), TraderAKey.class)
                .retrieve()
                .bodyToMono(TraderA.class)
                .onErrorResume((e) -> {
                    log.error("findTrader :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public List<String> deleteTrader(TraderAKey key) {
        return null;
    }

    public Flux<Gl> listenGl() {
        return accountApi.get()
                .uri(builder -> builder.path("/gl/receive")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(Gl.class);
    }

    public Mono<YearEnd> yearEnd(YearEnd t) {
        return accountApi.post()
                .uri("/account/yearEnd")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(t), YearEnd.class)
                .retrieve()
                .bodyToMono(YearEnd.class);
    }

    public Mono<List<ChartOfAccount>> searchCOA(String str, int level) {
        if (localDatabase) {
            return h2Repo.searchCOA(str, level);
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/searchCOA")
                .queryParam("str", str)
                .queryParam("level", level)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .collectList();
    }

    public Mono<List<VDescription>> getDescription(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getDescription")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class)
                .collectList();
    }

    public Mono<List<VDescription>> getReference(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getReference")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class)
                .collectList();
    }

    public Mono<List<VDescription>> getBatchNo(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getBatchNo")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class)
                .collectList();
    }

    public Flux<Gl> searchJournal(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/searchJournal")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .onErrorResume((e) -> {
                    log.info("searchJournal : " + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<StockOP>> searchOP(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/searchStockOP")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(StockOP.class)
                .collectList();
    }

    public Flux<OpeningBalance> getOpeningBalance(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/getOpening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(OpeningBalance.class);
    }

    public Mono<ReturnObject> getReport(ReportFilter filter) {
        if (filter.isSecond()) {
            return accountApiSecond.post()
                    .uri("/report/getReport")
                    .body(Mono.just(filter), ReportFilter.class)
                    .retrieve()
                    .bodyToMono(ReturnObject.class);
        }
        return accountApi.post()
                .uri("/report/getReport")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
    }

    public Mono<List<VApar>> getArAp(ReportFilter filter) {
        return accountApi.post()
                .uri("/report/getArAp")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VApar.class)
                .collectList();
    }

    public Mono<List<VTriBalance>> getTri(ReportFilter filter) {
        return accountApi.post()
                .uri("/report/getTriBalance")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VTriBalance.class)
                .collectList();
    }

    public Mono<List<Gl>> searchGl(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/searchGl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Flux<Gl> searchGlFlux(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/searchGl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .onErrorResume((t) -> Flux.empty());
    }

    public Flux<Gl> searchVoucher(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/searchVoucher")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .onErrorResume((t) -> Flux.empty());
    }

    public Mono<List<DateModel>> getDate() {
        if (localDatabase) {
            List<DateModel> list = h2Repo.getDate();
            if (!list.isEmpty()) {
                Mono.just(list);
            }
        }
        String startDate = Util1.toDateStrMYSQL(Global.startDate, "dd/MM/yyyy");
        boolean isAll = Util1.getBoolean(ProUtil.getProperty(ProUtil.DISABLE_ALL_FILTER));
        return accountApi.get()
                .uri(builder -> builder.path("/account/getDate")
                .queryParam("startDate", startDate)
                .queryParam("compCode", Global.compCode)
                .queryParam("isAll", isAll)
                .build())
                .retrieve()
                .bodyToFlux(DateModel.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getDate : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VoucherInfo>> getIntegrationVoucher(String fromDate, String toDate, String tranSource) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getIntegrationVoucher")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .queryParam("compCode", Global.compCode)
                .queryParam("tranSource", tranSource)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList()
                .doOnError((e) -> {
                    log.error("getIntegrationVoucher :" + e.getMessage());
                });

    }

    public Mono<List<TraderGroup>> getTraderGroup() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getTraderGroup")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(TraderGroup.class)
                .collectList();
    }

    public Mono<TraderGroup> findTraderGroup(String groupCode) {
        TraderGroupKey key = new TraderGroupKey();
        key.setGroupCode(Util1.isNull(groupCode, "-"));
        key.setCompCode(Global.compCode);
        return accountApi.post()
                .uri("/account/findTraderGroup")
                .body(Mono.just(key), TraderGroupKey.class)
                .retrieve()
                .bodyToMono(TraderGroup.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TraderGroup> saveTraderGroup(TraderGroup t) {
        return accountApi.post()
                .uri("/account/saveTraderGroup")
                .body(Mono.just(t), TraderGroup.class)
                .retrieve()
                .bodyToMono(TraderGroup.class);

    }

    public Flux<String> getShootTriMessage() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/shootTri")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Flux<Message> receiveMessage() {
        return accountApi.get().uri(builder -> builder.path("/message/receive")
                .queryParam("messageId", Global.macId)
                .build())
                .retrieve()
                .bodyToFlux(Message.class);
    }

    public Mono<String> sendDownloadMessage(String entity, String message) {
        Message mg = new Message();
        mg.setHeader(MessageType.DOWNLOAD);
        mg.setEntity(entity);
        mg.setMessage(message);
        return accountApi.post()
                .uri("/message/send")
                .body(Mono.just(mg), Message.class)
                .retrieve()
                .bodyToMono(String.class);
    }

}
