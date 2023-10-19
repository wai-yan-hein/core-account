/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.SelectionObserver;
import com.inventory.model.Message;
import com.inventory.model.MessageType;
import com.repo.AccountRepo;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class SSEListener {

    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CloudIntegration integration;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void start() {
        startUser();
        startInventory();
        startAccount();
    }

    private void startInventory() {
        inventoryRepo.receiveMessage().subscribe((t) -> {
            String header = t.getHeader();
            String entity = t.getEntity();
            log.info(header + "-" + entity);
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
//                        case MessageType.FORMULA ->
//                            //integration.downloa();
                        case MessageType.CRITERIA ->
                            integration.downloadStockCriteria();
                    }
                    showMessage(t.getMessage());
                }
            }
        }, (e) -> {
            log.error("startInventory : " + e.getMessage());
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
                        case MessageType.DATE_LOCK -> {
                            integration.downloadDateLock();
                        }

                    }
                    showMessage(t.getMessage());
                }
                case "PROGRAM_UPDATE" -> {
                    observer.selected("PROGRAM_UPDATE", "PROGRAM_UPDATE");
                }
            }

        }, (e) -> {
            log.error("startAccount : " + e.getMessage());
        });
    }

    private void showMessage(String message) {
        CoreAccountApplication.tray.showMessage(message);
    }

}
