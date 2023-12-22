/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.H2Repo;
import com.acc.model.BusinessType;
import com.acc.model.DeleteObj;
import com.common.Global;
import com.common.ReturnObject;
import com.common.Util1;
import com.user.model.RoleProperty;
import com.inventory.model.AppRole;
import com.inventory.model.Country;
import com.user.model.AppUser;
import com.user.model.DepartmentUser;
import com.user.model.MachineInfo;
import com.inventory.model.Message;
import com.inventory.model.MessageType;
import com.inventory.model.VRoleMenu;
import com.user.model.AuthenticationResponse;
import com.user.model.SysProperty;
import com.user.model.Currency;
import com.user.model.DateLock;
import com.user.model.DepartmentKey;
import com.user.model.ExchangeKey;
import com.user.model.ExchangeRate;
import com.user.model.MachineProperty;
import com.user.model.Menu;
import com.user.model.MenuTemplate;
import com.user.model.MenuTemplateKey;
import com.user.model.PrivilegeCompany;
import com.user.model.PrivilegeMenu;
import com.user.model.Project;
import com.user.model.ProjectKey;
import com.user.model.CompanyInfo;
import com.user.model.YearEnd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class UserRepo {

    @Autowired
    private WebClient userApi;
    @Autowired
    private H2Repo h2Repo;
    @Autowired
    private boolean localdatabase;

    public Mono<List<Currency>> getCurrency() {
        if (localdatabase) {
            return h2Repo.getCurrency();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getCurrency")
                .build())
                .retrieve()
                .bodyToFlux(Currency.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getCurrency :" + e.getMessage());
                    return Mono.empty();
                });

    }

    public Mono<List<CompanyInfo>> getCompany(boolean active) {
        if (localdatabase) {
            return h2Repo.getCompany(active);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getCompany")
                .queryParam("active", active)
                .build())
                .retrieve()
                .bodyToFlux(CompanyInfo.class)
                .collectList();
    }

    public Mono<List<AppUser>> getAppUser() {
        if (localdatabase) {
            return h2Repo.getAppUser();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getAppUser")
                .build())
                .retrieve().bodyToFlux(AppUser.class)
                .collectList();
    }

    public Mono<List<MachineInfo>> getMacList() {
        if (localdatabase) {
            return h2Repo.getMacList();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getMacList")
                .build())
                .retrieve().bodyToFlux(MachineInfo.class).collectList();
    }

    public Mono<List<AppRole>> getAppRole() {
        if (localdatabase) {
            return h2Repo.getAppRole(Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getRole")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(AppRole.class).collectList();
    }

    public Mono<MachineInfo> checkSerialNo(String serialNo) {
        return userApi.get()
                .uri(builder -> builder.path("/auth/checkSerialNo")
                .queryParam("serialNo", serialNo)
                .build())
                .retrieve()
                .bodyToMono(MachineInfo.class)
                .onErrorResume((e) -> {
                    if (localdatabase) {
                        return h2Repo.getMachineInfo(serialNo);
                    }
                    log.error("register : " + e.getMessage());
                    return Mono.empty();
                });

    }

    public Mono<MachineInfo> saveMachine(MachineInfo info) {
        return userApi.post()
                .uri("/user/saveMachine")
                .body(Mono.just(info), MachineInfo.class)
                .retrieve()
                .bodyToMono(MachineInfo.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                }).onErrorResume((t) -> Mono.empty());
    }

    public Mono<AuthenticationResponse> register(MachineInfo mac) {
        return userApi.post()
                .uri("/auth/registerMac")
                .body(Mono.just(mac), MachineInfo.class)
                .retrieve()
                .bodyToMono(AuthenticationResponse.class);
    }

    public String updateProgram() {
        Mono<ResponseEntity<String>> result = userApi.get()
                .uri(builder -> builder.path("/user/updateProgram")
                .queryParam("macId", Global.macId)
                .build())
                .retrieve().toEntity(String.class);
        return result.block().getBody();
    }

    public Mono<Boolean> updateAllMachine(boolean update) {
        return userApi.get()
                .uri(builder -> builder.path("/user/updateAllMachine")
                .queryParam("update", update)
                .build())
                .retrieve().bodyToMono(Boolean.class);
    }

    public Mono<CompanyInfo> saveCompany(CompanyInfo app) {
        return userApi.post()
                .uri("/user/saveCompany")
                .body(Mono.just(app), CompanyInfo.class)
                .retrieve()
                .bodyToMono(CompanyInfo.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<Currency> saveCurrency(Currency app) {
        return userApi.post()
                .uri("/user/saveCurrency")
                .body(Mono.just(app), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class)
                .onErrorResume((e) -> {
                    log.error("saveCurrency : " + e.getMessage());
                    return Mono.error(e);
                })
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<AppRole> saveAppRole(AppRole app) {
        return userApi.post()
                .uri("/user/saveRole")
                .body(Mono.just(app), AppRole.class)
                .retrieve()
                .bodyToMono(AppRole.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<PrivilegeCompany> savePrivilegeCompany(PrivilegeCompany app) {
        return userApi.post()
                .uri("/user/savePrivilegeCompany")
                .body(Mono.just(app), PrivilegeCompany.class)
                .retrieve()
                .bodyToMono(PrivilegeCompany.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<AppUser> saveUser(AppUser app) {
        return userApi.post()
                .uri("/user/saveUser")
                .body(Mono.just(app), AppUser.class)
                .retrieve()
                .bodyToMono(AppUser.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<Currency> findCurrency(String curCode) {
        if(Util1.isNullOrEmpty(curCode)){
            return Mono.empty();
        }
        if (localdatabase) {
            return h2Repo.findCurrency(curCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/findCurrency")
                .queryParam("curCode", curCode)
                .build())
                .retrieve()
                .bodyToMono(Currency.class)
                .onErrorResume((e) -> {
                    log.error("findCurrency :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<CompanyInfo> findCompany(String compCode) {
         if(Util1.isNullOrEmpty(compCode)){
            return Mono.empty();
        }
        if (localdatabase) {
            return h2Repo.findCompany(compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/findCompany")
                .queryParam("compCode", compCode)
                .build())
                .retrieve().bodyToMono(CompanyInfo.class);
    }

    public Mono<AppRole> finRole(String roleCode) {
         if(Util1.isNullOrEmpty(roleCode)){
            return Mono.empty();
        }
        if (localdatabase) {
            return h2Repo.finRole(roleCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/findRole")
                .queryParam("roleCode", roleCode)
                .build())
                .retrieve().bodyToMono(AppRole.class);
    }

    public Mono<DepartmentUser> findDepartment(Integer deptId) {
        if (Util1.isNullOrEmpty(deptId)) {
            return Mono.empty();
        }
        DepartmentKey key = new DepartmentKey();
        key.setDeptId(deptId);
        key.setCompCode(Global.compCode);
        if (localdatabase) {
            return h2Repo.findDepartment(key);
        }
        return userApi.post()
                .uri("/user/findDepartment")
                .body(Mono.just(key), DepartmentKey.class)
                .retrieve()
                .bodyToMono(DepartmentUser.class)
                .onErrorResume((e) -> {
                    log.error("findDepartment : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<DepartmentUser> saveDepartment(DepartmentUser d) {
        return userApi.post()
                .uri("/user/saveDepartment")
                .body(Mono.just(d), DepartmentUser.class)
                .retrieve()
                .bodyToMono(DepartmentUser.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<Currency> getDefaultCurrency() {
        return findCurrency(Global.currency);
    }

    public Mono<HashMap> setupProperty() {
        if (localdatabase) {
            return Mono.just(h2Repo.getProperty(Global.compCode, Global.roleCode, Global.macId));
        } else {
            return userApi.get()
                    .uri(builder -> builder.path("/user/getProperty")
                    .queryParam("compCode", Global.compCode)
                    .queryParam("roleCode", Global.roleCode)
                    .queryParam("macId", Global.macId)
                    .build())
                    .retrieve()
                    .bodyToMono(HashMap.class);

        }

    }

    public Mono<List<SysProperty>> getSystemProperty() {
        if (localdatabase) {
            return h2Repo.getSysProperty(Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getSystemProperty")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(SysProperty.class)
                .collectList();
    }

    public HashMap<String, String> getRoleProperty(String roleCode) {
        HashMap<String, String> hm = new HashMap<>();
        List<RoleProperty> prop;
        if (localdatabase) {
            prop = h2Repo.getRoleProperty(roleCode, Global.compCode);
        } else {
            Mono<ResponseEntity<List<RoleProperty>>> result = userApi.get()
                    .uri(builder -> builder.path("/user/getRoleProperty")
                    .queryParam("roleCode", roleCode)
                    .queryParam("compCode", Global.compCode)
                    .build())
                    .retrieve().toEntityList(RoleProperty.class);
            prop = result.block().getBody();
        }
        prop.forEach((t) -> {
            hm.put(t.getKey().getPropKey(), t.getPropValue());
        });
        return hm;
    }

    public HashMap<String, String> getMachineProperty(Integer macId) {
        HashMap<String, String> hm = new HashMap<>();
        List<MachineProperty> prop;
        if (macId == null) {
            return hm;
        }
        if (localdatabase) {
            prop = h2Repo.getMacProperty(macId);
        } else {
            Mono<ResponseEntity<List<MachineProperty>>> result = userApi.get()
                    .uri(builder -> builder.path("/user/getMacProperty")
                    .queryParam("macId", macId)
                    .build())
                    .retrieve().toEntityList(MachineProperty.class);
            prop = result.block().getBody();
        }
        prop.forEach((t) -> {
            hm.put(t.getKey().getPropKey(), t.getPropValue());
        });
        return hm;
    }

    public Mono<SysProperty> saveSys(SysProperty prop) {
        return userApi.post()
                .uri("/user/saveSystemProperty")
                .body(Mono.just(prop), SysProperty.class)
                .retrieve()
                .bodyToMono(SysProperty.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<RoleProperty> saveRoleProperty(RoleProperty prop) {
        return userApi.post()
                .uri("/user/saveRoleProperty")
                .body(Mono.just(prop), RoleProperty.class)
                .retrieve()
                .bodyToMono(RoleProperty.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<MachineProperty> saveMacProp(MachineProperty prop) {
        return userApi.post()
                .uri("/user/saveMacProperty")
                .body(Mono.just(prop), MachineProperty.class)
                .retrieve()
                .bodyToMono(MachineProperty.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<List<DepartmentUser>> getDeparment(Boolean active) {
        if (localdatabase) {
            return h2Repo.getDeparment(active, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getDepartment")
                .queryParam("active", active)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(DepartmentUser.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getDeparment :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<List<VRoleMenu>> getReport(String menuClass) {
        if (localdatabase) {
            return h2Repo.getReport(Global.roleCode, menuClass, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getReport")
                .queryParam("roleCode", Global.roleCode)
                .queryParam("menuClass", menuClass)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(VRoleMenu.class).collectList();
    }

    public Mono<List<BusinessType>> getBusinessType() {
        if (localdatabase) {
            return h2Repo.getBusinessType();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getBusinessType")
                .build())
                .retrieve()
                .bodyToFlux(BusinessType.class)
                .collectList();
    }

    public Mono<BusinessType> save(BusinessType type) {
        return userApi.post()
                .uri("/user/saveBusinessType")
                .body(Mono.just(type), BusinessType.class)
                .retrieve()
                .bodyToMono(BusinessType.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<DateLock> save(DateLock type) {
        return userApi.post()
                .uri("/user/saveDateLock")
                .body(Mono.just(type), DateLock.class)
                .retrieve()
                .bodyToMono(DateLock.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<List<DateLock>> getDateLock() {
        if (localdatabase) {
            return h2Repo.getDateLock();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getDateLock")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(DateLock.class)
                .collectList();
    }

    public Mono<BusinessType> find(Integer id) {
        if (localdatabase) {
            return h2Repo.find(id);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/findBusinessType")
                .queryParam("id", id)
                .build())
                .retrieve()
                .bodyToMono(BusinessType.class);
    }

    public Mono<YearEnd> yearEnd(YearEnd end) {
        return userApi.post()
                .uri("/user/yearEnd")
                .body(Mono.just(end), YearEnd.class)
                .retrieve()
                .bodyToMono(YearEnd.class);
    }

    public Mono<Menu> save(Menu menu) {
        return userApi.post()
                .uri("/user/saveMenu")
                .body(Mono.just(menu), Menu.class)
                .retrieve()
                .bodyToMono(Menu.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<List<Menu>> getMenuTree() {
        if (localdatabase) {
            return h2Repo.getMenuTree(Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getMenuTree")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Menu.class)
                .collectList();
    }

    public Mono<Country> findCountry(String code) {
        if (Util1.isNullOrEmpty(code)) {
            return Mono.empty();
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/findCountry")
                .queryParam("id", code)
                .build())
                .retrieve()
                .bodyToMono(Country.class);
    }

    public Mono<List<Country>> getCountry() {
        return userApi.get()
                .uri(builder -> builder.path("/user/getCountry")
                .build())
                .retrieve()
                .bodyToFlux(Country.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(Menu menu) {
        return userApi.post()
                .uri("/user/deleteMenu")
                .body(Mono.just(menu), Menu.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess((t) -> {
                    if (localdatabase && t) {
                        h2Repo.delete(menu);
                    }
                });
    }

    public Mono<List<Menu>> getMenuParent() {
        if (localdatabase) {
            return h2Repo.getMenuParent(Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getMenuParent")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Menu.class)
                .collectList();
    }

    public Mono<List<MenuTemplate>> getMenuTreeTemplate(Integer busId) {
        return userApi.get()
                .uri(builder -> builder.path("/template/getMenuTree")
                .queryParam("busId", busId)
                .build())
                .retrieve()
                .bodyToFlux(MenuTemplate.class)
                .collectList();
    }

    public Mono<List<MenuTemplate>> getMenuTemplate(Integer busId) {
        return userApi.get()
                .uri(builder -> builder.path("/template/getMenu")
                .queryParam("busId", busId)
                .build())
                .retrieve()
                .bodyToFlux(MenuTemplate.class)
                .collectList();
    }

    public Mono<Boolean> delete(MenuTemplateKey key) {
        return userApi.post()
                .uri("/template/deleteMenu")
                .body(Mono.just(key), MenuTemplate.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(int macId) {
        return userApi.delete()
                .uri(uriBuilder -> uriBuilder.path("/user/deleteMac")
                .queryParam("macId", macId)
                .build())
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<MenuTemplate> save(MenuTemplate type) {
        return userApi.post()
                .uri("/template/saveMenu")
                .body(Mono.just(type), MenuTemplate.class)
                .retrieve()
                .bodyToMono(MenuTemplate.class);

    }

    public Mono<List<Project>> searchProject() {
        if (localdatabase) {
            return h2Repo.getProject(Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/searchProject")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(Project.class)
                .collectList();
    }

    public Mono<Project> find(ProjectKey key) {
        if (localdatabase) {
            return h2Repo.find(key);
        }
        return userApi.post()
                .uri("/user/findProject")
                .body(Mono.just(key), ProjectKey.class)
                .retrieve()
                .bodyToMono(Project.class);
    }

    public Mono<Project> save(Project obj) {
        return userApi.post()
                .uri("/user/saveProject")
                .body(Mono.just(obj), Project.class)
                .retrieve()
                .bodyToMono(Project.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<ExchangeRate> save(ExchangeRate obj) {
        return userApi.post()
                .uri("/user/saveExchange")
                .body(Mono.just(obj), ExchangeRate.class)
                .retrieve()
                .bodyToMono(ExchangeRate.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(t);
                    }
                });
    }

    public Mono<ExchangeRate> findExchange(String exCode) {
        ExchangeKey key = new ExchangeKey();
        key.setExCode(exCode);
        key.setCompCode(Global.compCode);
        return userApi.post()
                .uri("/user/findExchange")
                .body(Mono.just(key), ExchangeKey.class)
                .retrieve()
                .bodyToMono(ExchangeRate.class);
    }

    public Mono<List<Project>> searchProjectByCode(String code) {
        if (localdatabase) {
            return h2Repo.searchProjectByCode(code, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/searchProjectByCode")
                .queryParam("compCode", Global.compCode)
                .queryParam("code", code)
                .build())
                .retrieve()
                .bodyToFlux(Project.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("searchProjectByCode :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<ExchangeRate>> searchExchange(String startDate, String endDate, String targetCur) {
        if (localdatabase) {
            return h2Repo.search(startDate, endDate, targetCur, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/searchExchange")
                .queryParam("compCode", Global.compCode)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .queryParam("targetCur", targetCur)
                .build())
                .retrieve()
                .bodyToFlux(ExchangeRate.class)
                .collectList();
    }

    public Mono<Boolean> delete(ExchangeKey obj) {
        return userApi.post()
                .uri("/user/delete-exchange")
                .body(Mono.just(obj), DeleteObj.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<List<AppUser>> getAppUserByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getUserByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(AppUser.class)
                .collectList();
    }

    public Mono<List<BusinessType>> getBusinessTypeByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getBusinessTypeByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(BusinessType.class)
                .collectList();
    }

    public Mono<List<CompanyInfo>> getCompanyInfoByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getCompanyInfoByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(CompanyInfo.class)
                .collectList();
    }

    public Mono<List<Currency>> getCurrencyByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getCurrencyByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(Currency.class)
                .collectList();
    }

    public Mono<List<DepartmentUser>> getDepartmentByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getDepartmentByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(DepartmentUser.class)
                .collectList();
    }

    public Mono<List<ExchangeRate>> getExchangeRateByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getExchangeRateByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(ExchangeRate.class)
                .collectList();
    }

    public Mono<List<ExchangeRate>> getExchangeRate() {
        return userApi.get()
                .uri(builder -> builder.path("/user/getExchangeRate")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ExchangeRate.class)
                .collectList();
    }

    public Mono<List<MachineProperty>> getMacPropertyByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getMacPropertyByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(MachineProperty.class)
                .collectList();
    }

    public Mono<List<MachineInfo>> getMachineInfoByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getMachineInfoByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(MachineInfo.class)
                .collectList();
    }

    public Mono<List<Menu>> getMenuByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getMenuByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(Menu.class)
                .collectList();
    }

    public Mono<List<PrivilegeCompany>> getPCByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getPCByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(PrivilegeCompany.class)
                .collectList();
    }

    public Mono<List<PrivilegeMenu>> getPMByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getPMByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(PrivilegeMenu.class)
                .collectList();
    }

    public Mono<List<Project>> getProjectByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getProjectByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(Project.class)
                .collectList();
    }

    public Mono<List<AppRole>> getRoleByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getRoleByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(AppRole.class)
                .collectList();
    }

    public Mono<List<RoleProperty>> getRolePropByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getRolePropByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(RoleProperty.class)
                .collectList();
    }

    public Mono<List<SysProperty>> getSystemPropertyByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getSystemPropertyByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(SysProperty.class)
                .collectList();
    }

    public Mono<List<DateLock>> getDateLockByDate(String updatedDate) {
        return userApi.get()
                .uri(builder -> builder.path("/user/getDateLockByDate")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve().bodyToFlux(DateLock.class)
                .collectList();
    }

    public Mono<AppUser> login(String userName, String password) {
        if (localdatabase) {
            return h2Repo.login(userName, password);
        }
        return userApi.get().uri(builder -> builder.path("/user/login")
                .queryParam("userName", userName)
                .queryParam("password", password)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.empty()
                ).bodyToMono(AppUser.class);
    }

    public Mono<List<CompanyInfo>> getPrivilegeRoleCompany(String roleCode) {
        if (localdatabase) {
            return h2Repo.getPrivilegeCompany(roleCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getPrivilegeRoleCompany")
                .queryParam("roleCode", roleCode)
                .build())
                .retrieve()
                .bodyToFlux(CompanyInfo.class)
                .collectList();
    }

    public Mono<List<VRoleMenu>> getPrivilegeRoleMenuTree(String roleCode) {
        if (localdatabase) {
            return h2Repo.getPrivilegeMenu(roleCode, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getPrivilegeRoleMenuTree")
                .queryParam("roleCode", roleCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VRoleMenu.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getRoleMenu : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VRoleMenu>> getRoleMenuTree(String roleCode) {
        if (localdatabase) {
            return h2Repo.getRoleMenu(roleCode, Global.compCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getRoleMenuTree")
                .queryParam("compCode", Global.compCode)
                .queryParam("roleCode", roleCode)
                .build())
                .retrieve()
                .bodyToFlux(VRoleMenu.class)
                .collectList();
    }

    public Mono<List<PrivilegeCompany>> getPrivilegeCompany(String roleCode) {
        if (localdatabase) {
            return h2Repo.searchCompany(roleCode);
        }
        return userApi.get()
                .uri(builder -> builder.path("/user/getPrivilegeCompany")
                .queryParam("roleCode", roleCode)
                .build())
                .retrieve()
                .bodyToFlux(PrivilegeCompany.class)
                .collectList();

    }

    public Mono<PrivilegeMenu> savePrivilegeMenu(PrivilegeMenu pm) {
        return userApi.post()
                .uri("/user/savePrivilegeMenu")
                .body(Mono.just(pm), PrivilegeMenu.class)
                .retrieve()
                .bodyToMono(PrivilegeMenu.class)
                .doOnSuccess((t) -> {
                    if (localdatabase) {
                        h2Repo.save(pm);
                    }
                });
    }

    public Mono<ReturnObject> getUpdatedProgramDate(String program) {
        return userApi.get().uri(builder -> builder.path("/download/getUpdatedProgramDate")
                .queryParam("program", program)
                .build())
                .retrieve()
                .bodyToMono(ReturnObject.class);
    }

    public Mono<byte[]> downloadProgram(String program) {
        return userApi.get().uri(builder -> builder.path("/download/program")
                .queryParam("program", program)
                .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class)
                .doOnNext((t) -> {
                    log.info("next");
                });
    }

    public Mono<String> sendMessage(Message message) {
        return userApi.post()
                .uri("/message/send")
                .body(Mono.just(message), Message.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> sendDownloadMessage(String entity, String message) {
        Message mg = new Message();
        mg.setHeader(MessageType.DOWNLOAD);
        mg.setEntity(entity);
        mg.setMessage(message);
        return userApi.post()
                .uri("/message/send")
                .body(Mono.just(mg), Message.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Flux<Message> receiveMessage() {
        return userApi.get().uri(builder -> builder.path("/message/receive")
                .queryParam("messageId", Global.macId)
                .build())
                .retrieve()
                .bodyToFlux(Message.class);
    }

    public Mono<List<Currency>> getCurrency(String homeCur, String exCur) {
        Mono<Currency> m1 = findCurrency(homeCur);
        Mono<Currency> m2 = findCurrency(exCur);
        return Mono.zip(m1, m2)
                .map(tuple -> {
                    List<Currency> combinedList = new ArrayList<>();
                    combinedList.add(tuple.getT1());
                    combinedList.add(tuple.getT2());
                    return combinedList;
                });
    }
}
