/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.mq;

import com.Tray;
import com.common.Global;
import com.inventory.ui.entry.dialog.ReorderAlertDialog;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class MessageSubscriber implements MessageListener {

    private ReorderAlertDialog reorderAlertDialog;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private Tray tray;

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage m) {
            try {
                String type = m.getString("MSG_TYPE");
                byte[] file = m.getBytes("FILE");
                log.info(String.format("onMessage %s", type));
                switch (type) {
                    case "REORDER" -> {
                        taskExecutor.execute(() -> {
                            String msg = "Reorder Level Notification";
                            tray.showMessage(msg);
                            if (reorderAlertDialog != null) {
                                if (reorderAlertDialog.isDisplayable()) {
                                    reorderAlertDialog.dispose();
                                }
                            }

                            reorderAlertDialog = new ReorderAlertDialog(Global.parentForm);
                            reorderAlertDialog.setReorderList(file);
                            reorderAlertDialog.setLocationRelativeTo(null);
                            reorderAlertDialog.setVisible(true);
                        });
                    }
                }
            } catch (JMSException e) {
                log.error(e.getMessage());
            }
        }
    }

}
