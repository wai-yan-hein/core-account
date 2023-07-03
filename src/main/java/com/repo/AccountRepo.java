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
import com.acc.model.ReportFilter;
import com.acc.model.StockOP;
import com.acc.model.TmpOpening;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.acc.model.StockOPKey;
import com.acc.model.VApar;
import com.acc.model.VDescription;
import com.acc.model.VTriBalance;
import com.common.FilterObject;
import com.common.Global;
import com.common.ProUtil;
import com.common.ReturnObject;
import com.common.Util1;
import com.model.VoucherInfo;
import com.user.model.YearEnd;
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
    private boolean localDatabase;
    @Autowired
    private H2Repo h2Repo;

    public Mono<DepartmentA> getDefaultDepartment() {
        String deptCode = Global.hmRoleProperty.get("default.department");
        return findDepartment(Util1.isNull(deptCode, "-"));
    }

    public Mono<ChartOfAccount> getDefaultCash() {
        String coaCode = Global.hmRoleProperty.get("default.cash");
        return findCOA(Util1.isNull(coaCode, "-"));
    }

    public Mono<DepartmentA> findDepartment(String deptCode) {
        DepartmentAKey key = new DepartmentAKey();
        key.setDeptCode(Util1.isNull(deptCode, "-"));
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return accountApi.post()
                .uri("/account/find-department")
                .body(Mono.just(key), DepartmentAKey.class)
                .retrieve()
                .bodyToMono(DepartmentA.class)
                .onErrorResume((e) -> {
                    log.error("findDepartment : " + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<List<DepartmentA>> getDepartment() {
        if (localDatabase) {
            return h2Repo.getDepartmentAccount();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-department")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(DepartmentA.class)
                .collectList();
    }

    public Flux<TraderA> getTrader() {
        if (localDatabase) {
            return h2Repo.getTraderAccount();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-trader")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(TraderA.class);
    }

    public Flux<TraderA> getTrader(String text) {
        if (localDatabase) {
            return h2Repo.findTraderAccount(text);
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/search-trader")
                .queryParam("compCode", Global.compCode)
                .queryParam("text", text)
                .build())
                .retrieve().bodyToFlux(TraderA.class);
    }

    public Flux<ChartOfAccount> getChartOfAccount() {
        if (localDatabase) {
            return h2Repo.getChartofAccount();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class);
    }

    public Flux<ChartOfAccount> getCOATree() {
        if (localDatabase) {
            return h2Repo.getCOATree();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-coa-tree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class);
    }

    public Flux<ChartOfAccount> getTraderAccount() {
        if (localDatabase) {
            return h2Repo.getTraderCOA();
        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-trader-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class);
    }

    public Mono<List<Gl>> getTranSource() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-tran-source")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Mono<Gl> save(Gl gl) {
        return accountApi.post()
                .uri("/account/save-gl")
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
                            return h2Repo.save(gl);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<Gl> uploadGL(Gl gl) {
        return accountApi.post()
                .uri("/account/save-gl")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(Gl.class);
    }

    public Mono<ReturnObject> saveGl(List<Gl> gl) {
        return accountApi.post()
                .uri("/account/save-gl-list")
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
                            return h2Repo.save(gl);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<Boolean> delete(COATemplateKey obj) {
        return accountApi.post()
                .uri("/account/delete-coa-template")
                .body(Mono.just(obj), COATemplateKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(DeleteObj obj) {
        return accountApi.post()
                .uri("/account/delete-gl")
                .body(Mono.just(obj), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(OpeningKey key) {
        return accountApi.post()
                .uri("/account/delete-op")
                .body(Mono.just(key), DeleteObj.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(COAKey key) {
        return accountApi.post()
                .uri("/account/delete-coa")
                .body(Mono.just(key), COAKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> deleteVoucher(DeleteObj gl) {
        return accountApi.post()
                .uri("/account/delete-voucher")
                .body(Mono.just(gl), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(StockOPKey key) {
        return accountApi.post()
                .uri("/account/delete-stock-op")
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
                .uri("/account/save-coa")
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

    public Mono<List<ChartOfAccount>> saveCOAFromTemplate(Integer busId, String compCode) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/save-coa-from-template")
                .queryParam("busId", busId)
                .queryParam("compCode", compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class)
                .collectList();
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
                .bodyToFlux(DepartmentA.class
                )
                .collectList();
    }

    public Double
            getTraderBalance(String date, String traderCode, String compCode) {
        try {
            return accountApi.get()
                    .uri(builder -> builder.path("/report/get-trader-balance")
                    .queryParam("date", date)
                    .queryParam("traderCode", traderCode)
                    .queryParam("compCode", compCode)
                    .build())
                    .retrieve()
                    .bodyToMono(Double.class
                    )
                    .block();
        } catch (Exception e) {
            log.error("getTraderBalance : " + e.getMessage());
        }
        return 0.0;
    }

    public Flux<COATemplate> getCOAChildTemplate(String coaCode, Integer busId) {
        return accountApi.get()
                .uri(builder -> builder.path("/template/getCOAChild")
                .queryParam("coaCode", coaCode)
                .queryParam("busId", busId)
                .build())
                .retrieve().bodyToFlux(COATemplate.class
                );
    }

    public Flux<ChartOfAccount> getCOAChild(String coaCode) {
        if (localDatabase) {
            return h2Repo.getCOAChild(coaCode);

        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-coa-child")
                .queryParam("coaCode", coaCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ChartOfAccount.class
                );
    }

    public Mono<List<ChartOfAccount>> getCOAByGroup(String groupCode) {
        if (localDatabase) {
            return h2Repo.getCOA3(groupCode);

        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOAByGroup")
                .queryParam("groupCode", groupCode == null ? "" : groupCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class
                )
                .onErrorResume((e) -> {
                    log.error(
                            "getCOAByGroup : " + e.getMessage());
                    return Mono.empty();
                }
                )
                .collectList();
    }

    public Mono<List<ChartOfAccount>> getCOAByHead(String headCode) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/getCOAByHead")
                .queryParam("headCode", headCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class
                )
                .onErrorResume((e) -> {
                    log.error(
                            "getCOAByHead : " + e.getMessage());
                    return Mono.empty();
                }
                )
                .collectList();
    }

    public Flux<Gl> getJournal(String vouNo) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-journal")
                .queryParam("glVouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Gl.class
                );
    }

    public Flux<Gl> getVoucher(String vouNo) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-voucher")
                .queryParam("glVouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(Gl.class
                );
    }

    public Mono<COATemplate> findCOATemplate(COATemplateKey key) {
        return accountApi.post()
                .uri("/template/find-coa-template")
                .body(Mono.just(key), COATemplateKey.class
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(COATemplate.class
                );
    }

    public Mono<ChartOfAccount> findCOA(String coaCode) {
        COAKey key = new COAKey();
        key.setCoaCode(coaCode);
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);

        }
        return accountApi.post()
                .uri("/account/find-coa")
                .body(Mono.just(key), COAKey.class
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ChartOfAccount.class
                )
                .onErrorResume((e) -> {
                    log.error(
                            "findCOA :" + e.getMessage());
                    return Mono.empty();
                }
                );
    }

    public Mono<DepartmentA> saveDepartment(DepartmentA dep) {
        return accountApi.post()
                .uri("/account/save-department")
                .body(Mono.just(dep), DepartmentA.class
                )
                .retrieve()
                .bodyToMono(DepartmentA.class
                )
                .onErrorResume((e) -> {
                    log.error(
                            "saveDepartment : " + e.getMessage());
                    return Mono.error(e);
                }
                )
                .doOnSuccess((t) -> {
                    h2Repo.save(t);
                });
    }

    public Flux<DepartmentA> getDepartmentTree() {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-department-tree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(DepartmentA.class
                );
    }

    public Mono<OpeningBalance> saveCOAOpening(OpeningBalance opening) {
        return accountApi.post()
                .uri("/account/save-opening")
                .body(Mono.just(opening), OpeningBalance.class
                )
                .retrieve()
                .bodyToMono(OpeningBalance.class
                );
    }

    public Mono<TmpOpening> getOpening(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/get-coa-opening")
                .body(Mono.just(filter), ReportFilter.class
                )
                .retrieve()
                .bodyToMono(TmpOpening.class
                )
                .onErrorResume((e) -> {
                    log.info(
                            "getOpening " + e.getMessage());
                    return Mono.empty();
                }
                );
    }

    public Mono<StockOP> save(StockOP op) {
        return accountApi.post()
                .uri("/account/save-stock-op")
                .body(Mono.just(op), StockOP.class
                )
                .retrieve()
                .bodyToMono(StockOP.class
                );
    }

    public Mono<TraderA> saveTrader(TraderA t) {
        return accountApi.post()
                .uri("/account/save-trader")
                .body(Mono.just(t), TraderA.class
                )
                .retrieve()
                .bodyToMono(TraderA.class
                )
                .doOnSuccess((trader) -> {
                    h2Repo.save(trader);
                }
                );
    }

    public List<String> deleteTrader(TraderAKey key) {
        return null;
    }

    public Flux<Gl> listenGl() {
        return accountApi.get()
                .uri(builder -> builder.path("/gl/receive")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(Gl.class
                );
    }

    public Mono<YearEnd> yearEnd(YearEnd t) {
        return accountApi.post()
                .uri("/account/yearEnd")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(t), YearEnd.class
                )
                .retrieve()
                .bodyToMono(YearEnd.class
                );
    }

    public Mono<List<ChartOfAccount>> searchCOA(String str, int level) {
        if (localDatabase) {
            return h2Repo.searchCOA(str, level);

        }
        return accountApi.get()
                .uri(builder -> builder.path("/account/search-coa")
                .queryParam("str", str)
                .queryParam("level", level)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ChartOfAccount.class
                )
                .collectList();
    }

    public Mono<List<VDescription>> getDescription(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-description")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class
                )
                .collectList();
    }

    public Mono<List<VDescription>> getReference(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-reference")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class
                )
                .collectList();
    }

    public Mono<List<TraderA>> searchTrader(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/search-trader")
                .queryParam("compCode", Global.compCode)
                .queryParam("text", str)
                .build())
                .retrieve()
                .bodyToFlux(TraderA.class
                )
                .collectList();
    }

    public Mono<List<VDescription>> getBatchNo(String str) {
        return accountApi.get()
                .uri(builder -> builder.path("/account/get-batch-no")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class
                )
                .collectList();
    }

    public Mono<List<Gl>> searchJournal(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/search-journal")
                .body(Mono.just(filter), ReportFilter.class
                )
                .retrieve()
                .bodyToFlux(Gl.class
                )
                .collectList();
    }

    public Mono<List<StockOP>> searchOP(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/search-stock-op")
                .body(Mono.just(filter), ReportFilter.class
                )
                .retrieve()
                .bodyToFlux(StockOP.class
                )
                .collectList();
    }

    public Mono<List<OpeningBalance>> getOpeningBalance(ReportFilter filter) {
        return accountApi.post()
                .uri("/account/get-opening")
                .body(Mono.just(filter), ReportFilter.class
                )
                .retrieve()
                .bodyToFlux(OpeningBalance.class
                )
                .collectList();
    }

    public Mono<ReturnObject> getReport(ReportFilter filter) {
        return accountApi
                .post()
                .uri("/report/get-report")
                .body(Mono.just(filter), FilterObject.class
                )
                .retrieve()
                .bodyToMono(ReturnObject.class
                );
    }

    public Mono<List<VApar>> getArAp(ReportFilter filter) {
        return accountApi.post()
                .uri("/report/get-arap")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VApar.class)
                .collectList();
    }

    public Mono<List<VTriBalance>> getTri(ReportFilter filter) {
        return accountApi.post()
                .uri("/report/get-tri-balance")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VTriBalance.class)
                .collectList();
    }

    public Mono<List<Gl>> searchGl(ReportFilter filter) {
        if (filter.isLocal()) {
            return h2Repo.searchGL(filter);

        }
        return accountApi.post()
                .uri("/account/search-gl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.info(
                            "searchGl " + e.getMessage());
                    return Mono.empty();
                }
                );
    }

    public Mono<List<Gl>> searchVoucher(ReportFilter filter) {
        return accountApi
                .post()
                .uri("/account/search-voucher")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList();
    }

    public Mono<List<DateModel>> getDate() {
        /* if (localDatabase) {
        return Mono.just(h2Repo.getDate());
        }*/
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
                    log.info("getDate " + e.getMessage());
                    return Mono.error(e);
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
}