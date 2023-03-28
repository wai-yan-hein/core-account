/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.common.Global;
import com.user.model.RoleProperty;
import com.common.Util1;
import com.inventory.model.AppRole;
import com.inventory.model.AppUser;
import com.user.model.DepartmentUser;
import com.inventory.model.MachineInfo;
import com.inventory.model.VRoleMenu;
import com.user.model.SysProperty;
import com.user.model.CompanyInfo;
import com.user.model.Currency;
import com.user.model.MachineProperty;
import com.user.model.PrivilegeCompany;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserRepo {

    int min = 1;
    @Autowired
    private WebClient userApi;
    private DepartmentUser department;
    private List<DepartmentUser> listDept;

    public Mono<List<Currency>> getCurrency() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-currency")
                .build())
                .retrieve().bodyToFlux(Currency.class).collectList();
    }

    public Mono<List<CompanyInfo>> getCompany() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-company")
                .build())
                .retrieve().bodyToFlux(CompanyInfo.class).collectList();
    }

    public Mono<List<AppUser>> getAppUser() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-appuser")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(AppUser.class).collectList();
    }

    public Mono<List<MachineInfo>> getMacList() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-mac-list")
                .build())
                .retrieve().bodyToFlux(MachineInfo.class).collectList();
    }

    public Mono<List<AppRole>> getAppRole() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-role")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(AppRole.class).collectList();
    }

    public Mono<MachineInfo> register(String macName) {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-mac-info")
                .queryParam("macName", macName)
                .build())
                .retrieve().bodyToMono(MachineInfo.class);

    }

    public Mono<MachineInfo> register(MachineInfo mac) {
        return userApi.post()
                .uri("/user/save-mac")
                .body(Mono.just(mac), MachineInfo.class)
                .retrieve()
                .bodyToMono(MachineInfo.class);
    }

    public String updateProgram() {
        Mono<ResponseEntity<String>> result = userApi.get()
                .uri(builder -> builder.path("/user/update-program")
                .queryParam("macId", Global.macId)
                .build())
                .retrieve().toEntity(String.class);
        return result.block().getBody();
    }

    public Mono<CompanyInfo> saveCompany(CompanyInfo app) {
        return userApi.post()
                .uri("/user/save-company")
                .body(Mono.just(app), CompanyInfo.class)
                .retrieve()
                .bodyToMono(CompanyInfo.class);
    }

    public Mono<Currency> saveCurrency(Currency app) {
        return userApi.post()
                .uri("/user/save-currency")
                .body(Mono.just(app), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class);
    }

    public Mono<AppRole> saveAppRole(AppRole app) {
        return userApi.post()
                .uri("/user/save-role")
                .body(Mono.just(app), AppRole.class)
                .retrieve()
                .bodyToMono(AppRole.class);
    }

    public Mono<PrivilegeCompany> saveCompRole(PrivilegeCompany app) {
        return userApi.post()
                .uri("/user/save-privilege-company")
                .body(Mono.just(app), PrivilegeCompany.class)
                .retrieve()
                .bodyToMono(PrivilegeCompany.class);
    }

    public Mono<AppUser> saveUser(AppUser app) {
        return userApi.post()
                .uri("/user/save-user")
                .body(Mono.just(app), AppUser.class)
                .retrieve()
                .bodyToMono(AppUser.class);
    }

    public Mono<Currency> findCurrency(String curCode) {
        return userApi.get()
                .uri(builder -> builder.path("/user/find-currency")
                .queryParam("curCode", curCode)
                .build())
                .retrieve().bodyToMono(Currency.class);
    }

    public Mono<DepartmentUser> findDepartment(Integer deptId) {
        return userApi.get()
                .uri(builder -> builder.path("/user/find-department")
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToMono(DepartmentUser.class);
    }

    public Mono<DepartmentUser> saveDepartment(DepartmentUser d) {
        return userApi.post()
                .uri("/user/save-department")
                .body(Mono.just(d), DepartmentUser.class)
                .retrieve()
                .bodyToMono(DepartmentUser.class);
    }

    public Mono<Currency> getDefaultCurrency() {
        String curCode = Global.hmRoleProperty.get("default.currency");
        return findCurrency(Util1.isNull(curCode, "-"));
    }

    public void setupProperty() {
        Mono<HashMap> result = userApi.get()
                .uri(builder -> builder.path("/user/get-property")
                .queryParam("compCode", Global.compCode)
                .queryParam("roleCode", Global.roleCode)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve().bodyToMono(HashMap.class);
        result.subscribe((t) -> {
            Global.hmRoleProperty = t;
            log.info("setupProperty.");
        });

    }

    public Mono<List<SysProperty>> getSystProperty() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-system-property")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(SysProperty.class).collectList();
    }

    public HashMap<String, String> getRoleProperty(String roleCode) {
        HashMap<String, String> hm = new HashMap<>();
        Mono<ResponseEntity<List<RoleProperty>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-role-property")
                .queryParam("roleCode", roleCode)
                .build())
                .retrieve().toEntityList(RoleProperty.class);
        List<RoleProperty> prop = result.block().getBody();
        prop.forEach((t) -> {
            hm.put(t.getKey().getPropKey(), t.getPropValue());
        });
        return hm;
    }

    public HashMap<String, String> getMachineProperty(Integer macId) {
        HashMap<String, String> hm = new HashMap<>();
        if (macId == null) {
            return hm;
        }
        Mono<ResponseEntity<List<MachineProperty>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-mac-property")
                .queryParam("macId", macId)
                .build())
                .retrieve().toEntityList(MachineProperty.class);
        List<MachineProperty> prop = result.block().getBody();
        prop.forEach((t) -> {
            hm.put(t.getKey().getPropKey(), t.getPropValue());
        });
        return hm;
    }

    public Mono<SysProperty> saveSys(SysProperty prop) {
        return userApi.post()
                .uri("/user/save-system-property")
                .body(Mono.just(prop), SysProperty.class)
                .retrieve()
                .bodyToMono(SysProperty.class);
    }

    public Mono<RoleProperty> saveRoleProperty(RoleProperty prop) {
        return userApi.post()
                .uri("/user/save-role-property")
                .body(Mono.just(prop), RoleProperty.class)
                .retrieve()
                .bodyToMono(RoleProperty.class);
    }

    public Mono<MachineProperty> saveMacProp(MachineProperty prop) {
        return userApi.post()
                .uri("/user/save-mac-property")
                .body(Mono.just(prop), MachineProperty.class)
                .retrieve()
                .bodyToMono(MachineProperty.class);
    }

    public Mono<List<DepartmentUser>> getDeparment() {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-department")
                .build())
                .retrieve().bodyToFlux(DepartmentUser.class).collectList();
    }

    public Mono<List<VRoleMenu>> getReport(String menuClass) {
        return userApi.get()
                .uri(builder -> builder.path("/user/get-report")
                .queryParam("roleCode", Global.roleCode)
                .queryParam("menuClass", menuClass)
                .build())
                .retrieve().bodyToFlux(VRoleMenu.class).collectList();
    }

}
