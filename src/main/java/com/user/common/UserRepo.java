/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.common;

import com.common.Global;
import com.common.RoleProperty;
import com.common.Util1;
import com.inventory.model.AppRole;
import com.inventory.model.AppUser;
import com.inventory.model.MachineInfo;
import com.user.model.CompanyInfo;
import com.user.model.Currency;
import com.user.model.PrivilegeCompany;
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

    public void initRoleProperty() {
        Mono<ResponseEntity<List<RoleProperty>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-role-property")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().toEntityList(RoleProperty.class);
        List<RoleProperty> roleProperty = result.block().getBody();
        if (!roleProperty.isEmpty()) {
            for (RoleProperty p : roleProperty) {
                Global.hmRoleProperty.put(p.getKey().getPropKey(), p.getPropValue());
            }
        }
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

}
