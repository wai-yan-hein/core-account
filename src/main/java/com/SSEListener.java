/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.repo.AccountRepo;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SSEListener {

    private final InventoryRepo inventoryRepo;
    private final AccountRepo accountRepo;
    private final UserRepo userRepo;
    private final CloudIntegration integration;
    private final PrinterIntegration printer;
    @Setter
    private SelectionObserver observer;

    public void start() {
        startUser();
        startInventory();
        startAccount();
    }

    private void startInventory() {
        inventoryRepo.receiveMessage().subscribe((message) -> {
            String header = message.getHeader();
            String entity = message.getEntity();
            int macId = Util1.getInteger(message.getMacId());
            log.info(header + "-" + entity);
            if (!Util1.isNullOrEmpty(header)) {
                switch (header) {
                    case MessageType.DOWNLOAD -> {
                        switch (entity) {
                            case MessageType.STOCK ->
                                integration.downloadStock();
                            case MessageType.TRADER_INV ->
                                integration.downloadInvTrader();
                            case MessageType.CATEGORY ->
                                integration.downloadCategory();
                            case MessageType.BRAND ->
                                integration.downloadBrand();
                            case MessageType.GROUP ->
                                integration.downloadStockType();
                            case MessageType.UNIT ->
                                integration.downloadUnit();
                            case MessageType.RELATION ->
                                integration.downloadRelation();
                            case MessageType.LOCATION ->
                                integration.downloadLocation();
                            case MessageType.REGION ->
                                integration.downloadRegion();
                            case MessageType.SALE_MAN ->
                                integration.downloadSaleMan();
                            case MessageType.VOU_STATUS ->
                                integration.downloadVouStatus();
                            case MessageType.ORDER_STATUS ->
                                integration.downloadOrderStatus();
                            case MessageType.PRICE_OPTION ->
                                integration.downloadPriceOption();
                            case MessageType.LABOUR_GROUP ->
                                integration.downloadLabourGroup();
                            case MessageType.FORMULA ->
                                integration.downloadStockFormula();
                            case MessageType.FORMULA_PRICE ->
                                integration.downloadStockFormulaPrice();
                            case MessageType.FORMULA_QTY ->
                                integration.downloadStockFormulaQty();
                            case MessageType.GRADE_DETAIL ->
                                integration.downloadGradeDetail();
                            case MessageType.CRITERIA ->
                                integration.downloadStockCriteria();
                            case MessageType.JOB ->
                                integration.downloadJob();
                            case MessageType.PATTERN ->
                                integration.downloadPattern();
                            case MessageType.STOCK_PRICE->
                                integration.downloadStockUnitPrice();
                        }
                        showMessage(message.getMessage());
                    }
                    case MessageType.PRINTER -> {
                        log.info("request mac id : " + macId);
                        if (macId == Global.macId) {
                            log.info("machine matched.");
                            switch (entity) {
                                case MessageType.SALE ->
                                    printer.printSale(message);
                                case MessageType.ORDER->
                                    printer.printOrder(message);
                            }
                        }
                    }
                }
            }
        });
    }

    private void startAccount() {
        accountRepo.receiveMessage().subscribe((t) -> {
            String header = t.getHeader();
            String entity = t.getEntity();
            log.info(header + "-" + entity);
            switch (header) {
                case MessageType.DOWNLOAD -> {
                    switch (entity) {
                        case MessageType.TRADER_ACC ->
                            integration.downloadTraderAccount();
                        case MessageType.DEPARTMENT_ACC ->
                            integration.downloadDepartmentAccount();
                        case MessageType.COA ->
                            integration.downloadChartofAccount();
                    }
                    showMessage(t.getMessage());
                }
            }
        }, (e) -> {
            log.error("startAccount : " + e.getMessage());
        });
    }

    private void startUser() {
        userRepo.receiveMessage().subscribe((t) -> {
            String header = t.getHeader();
            String entity = t.getEntity();
            log.info(header + "-" + entity);
            switch (header) {
                case MessageType.DOWNLOAD -> {
                    switch (entity) {
                        case MessageType.USER ->
                            integration.downloadAppUser();
                        case MessageType.BUSTYPE ->
                            integration.downloadBusinessType();
                        case MessageType.COMPANY ->
                            integration.downloadCompanyInfo();
                        case MessageType.CURRENCY ->
                            integration.downloadCurrency();
                        case MessageType.DEPARTMENT_USER ->
                            integration.downloadDepartment();
                        case MessageType.EXRATE ->
                            integration.downloadExchangeRate();
                        case MessageType.MACHINE_PROERTY ->
                            integration.downloadMacProperty();
                        case MessageType.MENU ->
                            integration.downloadMenu();
                        case MessageType.PRIVILEGE_COMPANY ->
                            integration.downloadPC();
                        case MessageType.PRIVILEGE_MENU ->
                            integration.downloadPM();
                        case MessageType.PROJECT ->
                            integration.downloadProject();
                        case MessageType.ROLE ->
                            integration.downloadRole();
                        case MessageType.ROLE_PROPERTY ->
                            integration.downloadRoleProperty();
                        case MessageType.SYSTEM_PROPERTY ->
                            integration.downloadSystemProperty();
                        case MessageType.DATE_LOCK ->
                            integration.downloadDateLock();
                        case MessageType.MACHINE -> {
                            observer.selected("message", "Program need to logout.");
                        }

                    }
                    showMessage(t.getMessage());
                }
                case "PROGRAM_UPDATE" -> {
                    observer.selected("PROGRAM_UPDATE", "PROGRAM_UPDATE");
                }
            }

        }, (e) -> {
            log.error("startUser : " + e.getMessage());
        });
    }

    private void showMessage(String message) {
        CoreAccountApplication.tray.showMessage(message);
    }

}
