/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.model.VoucherInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
@Slf4j
public class HMSRepo {

    @Autowired
    private WebClient hmsApi;

    public Mono<List<VoucherInfo>> getSaleList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getSaleList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getPurchaseList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getPurchaseList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getReturnInList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getReturnInList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getReturnOutList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getReturnOutList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getOPDList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getOPDList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getOTList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getOTList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getDCList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getDCList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<List<VoucherInfo>> getPaymentList(String fromDate, String toDate) {
        return hmsApi.get()
                .uri(builder -> builder.path("/getPaymentList")
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .build())
                .retrieve()
                .bodyToFlux(VoucherInfo.class)
                .collectList();
    }

    public Mono<String> syncToAccount(String transSource, String vouNo) {
        String url = "";
        switch (transSource) {
            case "SALE" ->
                url = "/sale?vouNo";
            case "PURCHASE" ->
                url = "/purchase?vouNo";
            case "RETURN_IN" ->
                url = "/returnIn?vouNo";
            case "RETURN_OUT" ->
                url = "/returnOut?vouNo";
            case "OPD" ->
                url = "/opd?vouNo";
            case "OT" ->
                url = "/ot?vouNo";
            case "DC" ->
                url = "/dc?vouNo";
            case "PAYMENT" ->
                url = "/payment?payId";
        }
        if (!url.isEmpty()) {
            return hmsApi.post()
                    .uri("" + url + "={vouNo}", vouNo)
                    .retrieve()
                    .bodyToMono(String.class);
        }
        return Mono.empty();
    }
}
