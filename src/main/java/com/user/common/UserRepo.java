/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.common.Global;
import com.common.RoleProperty;
import com.common.Util1;
import com.inventory.model.AppRole;
import com.inventory.model.MachineInfo;
import com.inventory.model.AppUser;
import com.user.model.Department;
import com.inventory.model.MachineInfo;
import com.inventory.model.VRoleMenu;
import com.user.model.SysProperty;
import com.user.model.CompanyInfo;
import com.user.model.Currency;
import com.user.model.MachineProperty;
import com.user.model.PrivilegeCompany;
import java.time.Duration;
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

    public List<Currency> getCurrency() {
        Mono<ResponseEntity<List<Currency>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-currency")
                .build())
                .retrieve().toEntityList(Currency.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<CompanyInfo> getCompany() {
        Mono<ResponseEntity<List<CompanyInfo>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-company")
                .build())
                .retrieve().toEntityList(CompanyInfo.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<AppUser> getAppUser() {
        Mono<ResponseEntity<List<AppUser>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-appuser")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(AppUser.class);
        List<AppUser> user = result.block(Duration.ofMinutes(min)).getBody();
        if (!user.isEmpty()) {
            for (AppUser app : user) {
                Global.hmUser.put(app.getUserCode(), app.getUserShortName());
            }
        }
        return user;
    }

    public List<MachineInfo> getMacList() {
        Mono<ResponseEntity<List<MachineInfo>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-mac-list")
                .build())
                .retrieve().toEntityList(MachineInfo.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<AppRole> getAppRole() {
        Mono<ResponseEntity<List<AppRole>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-role")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(AppRole.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public MachineInfo register(String macName) {
        Mono<ResponseEntity<MachineInfo>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-mac-info")
                .queryParam("macName", macName)
                .build())
                .retrieve().toEntity(MachineInfo.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public MachineInfo register(MachineInfo mac) {
        Mono<MachineInfo> result = userApi.post()
                .uri("/user/save-mac")
                .body(Mono.just(mac), MachineInfo.class)
                .retrieve()
                .bodyToMono(MachineInfo.class);
        return result.block(Duration.ofMinutes(min));
    }

    public String updateProgram() {
        Mono<ResponseEntity<String>> result = userApi.get()
                .uri(builder -> builder.path("/user/update-program")
                .queryParam("macId", Global.macId)
                .build())
                .retrieve().toEntity(String.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public CompanyInfo saveCompany(CompanyInfo app) {
        Mono<CompanyInfo> result = userApi.post()
                .uri("/user/save-company")
                .body(Mono.just(app), CompanyInfo.class)
                .retrieve()
                .bodyToMono(CompanyInfo.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Currency saveCurrency(Currency app) {
        Mono<Currency> result = userApi.post()
                .uri("/user/save-currency")
                .body(Mono.just(app), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class);
        return result.block(Duration.ofMinutes(min));
    }

    public AppRole saveAppRole(AppRole app) {
        Mono<AppRole> result = userApi.post()
                .uri("/user/save-role")
                .body(Mono.just(app), AppRole.class)
                .retrieve()
                .bodyToMono(AppRole.class);
        return result.block(Duration.ofMinutes(min));
    }

    public PrivilegeCompany saveCompRole(PrivilegeCompany app) {
        Mono<PrivilegeCompany> result = userApi.post()
                .uri("/user/save-privilege-company")
                .body(Mono.just(app), PrivilegeCompany.class)
                .retrieve()
                .bodyToMono(PrivilegeCompany.class);
        return result.block(Duration.ofMinutes(min));
    }

    public AppUser saveUser(AppUser app) {
        Mono<AppUser> result = userApi.post()
                .uri("/user/save-user")
                .body(Mono.just(app), AppUser.class)
                .retrieve()
                .bodyToMono(AppUser.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Currency findCurrency(String curCode) {
        Mono<ResponseEntity<Currency>> result = userApi.get()
                .uri(builder -> builder.path("/user/find-currency")
                .queryParam("curCode", curCode)
                .build())
                .retrieve().toEntity(Currency.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Currency getDefaultCurrency() {
        String curCode = Global.hmRoleProperty.get("default.currency");
        return findCurrency(Util1.isNull(curCode, "-"));
    }

    public void setupProperty(String roleCode, String compCode, Integer macId) {
        Mono<HashMap> result = userApi.get()
                .uri(builder -> builder.path("/user/get-property")
                .queryParam("roleCode", roleCode)
                .queryParam("compCode", compCode)
                .queryParam("macId", macId)
                .build())
                .retrieve().bodyToMono(HashMap.class);
        Global.hmRoleProperty = result.block();
        log.info("setupProperty.");
    }

    public HashMap<String, String> getSystProperty() {
        HashMap<String, String> hm = new HashMap<>();
        Mono<ResponseEntity<List<SysProperty>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-system-property")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(SysProperty.class);
        List<SysProperty> prop = result.block().getBody();
        prop.forEach((t) -> {
            hm.put(t.getKey().getPropKey(), t.getPropValue());
        });
        return hm;
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

    public SysProperty saveSys(SysProperty prop) {
        Mono<SysProperty> result = userApi.post()
                .uri("/user/save-system-property")
                .body(Mono.just(prop), SysProperty.class)
                .retrieve()
                .bodyToMono(SysProperty.class);
        return result.block();
    }

    public RoleProperty saveRoleProperty(RoleProperty prop) {
        Mono<RoleProperty> result = userApi.post()
                .uri("/user/save-role-property")
                .body(Mono.just(prop), RoleProperty.class)
                .retrieve()
                .bodyToMono(RoleProperty.class);
        return result.block();
    }

    public MachineProperty saveMacProp(MachineProperty prop) {
        Mono<MachineProperty> result = userApi.post()
                .uri("/user/save-mac-property")
                .body(Mono.just(prop), MachineProperty.class)
                .retrieve()
                .bodyToMono(MachineProperty.class);
        return result.block();
    }

    public List<Department> getDeparment() {
        Mono<ResponseEntity<List<Department>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-department")
                .build())
                .retrieve().toEntityList(Department.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<VRoleMenu> getReport(String menuClass) {
        Mono<ResponseEntity<List<VRoleMenu>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-report")
                .queryParam("roleCode", Global.roleCode)
                .queryParam("menuClass", menuClass)
                .build())
                .retrieve().toEntityList(VRoleMenu.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

}
