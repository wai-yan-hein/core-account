/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.inventory.model.MessageType;
import com.repo.InventoryRepo;
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
    private CloudIntegration integration;

    public void start() {
        startInventory();
    }

    private void startInventory() {
        inventoryRepo.receiveMessage().subscribe((t) -> {
            String header = t.getHeader();
            String entity = t.getEntity();
            log.info(header);
            switch (header) {
                case MessageType.DOWNLOAD -> {
                    switch (entity) {
                        case MessageType.STOCK ->
                            integration.downloadStock();
                        case MessageType.TRADER ->
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
                        /* case MessageType.REGION ->
                            //integration.downloadLocation();
                            case MessageType.TRADER_GROUP ->
                            //integration.downloadLocation();*/

                    }
                    showMessage(t.getMessage());
                }
            }
        }, (e) -> {
            log.error("startInventory : " + e.getMessage());
        });
    }

    private void showMessage(String message) {
        CoreAccountApplication.tray.showMessage(message);
    }

}
