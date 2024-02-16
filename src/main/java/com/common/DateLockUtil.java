/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.repo.UserRepo;
import com.user.model.DateLock;
import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
@RequiredArgsConstructor
@org.springframework.stereotype.Component
public class DateLockUtil {

    private final UserRepo userRepo;
    public static List<DateLock> listLock;
    public static final String MESSAGE = "Data Lock by management.";

    public void initLockDate() {
        log.info("initLockDate.");
        userRepo.getDateLock().doOnSuccess((t) -> {
            t.removeIf(c -> !c.isDateLock());
            listLock = t;
        }).subscribe();
    }

    public static boolean isLockDate(Date date) {
        if (listLock != null) {
            for (DateLock dateLock : listLock) {
                LocalDate startDate = dateLock.getStartDate();
                LocalDate endDate = dateLock.getEndDate();
                LocalDate checkDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                // Compare only the date portion (ignoring time)
                if (checkDate.isEqual(startDate) || checkDate.isEqual(endDate)
                        || (checkDate.isAfter(startDate) && checkDate.isBefore(endDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isLockDate(LocalDateTime date) {
        if (listLock != null) {
            LocalDate userDate = date.toLocalDate();
            for (DateLock dateLock : listLock) {
                LocalDate startDate = dateLock.getStartDate();
                LocalDate endDate = dateLock.getEndDate();
                if ((userDate.isEqual(startDate) || userDate.isEqual(endDate))
                        || (userDate.isAfter(startDate) && userDate.isBefore(endDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isLockDate(LocalDate date) {
        if (listLock != null) {
            for (DateLock dateLock : listLock) {
                LocalDate startDate = dateLock.getStartDate();
                LocalDate endDate = dateLock.getEndDate();
                if ((date.isEqual(startDate) || date.isEqual(endDate))
                        || (date.isAfter(startDate) && date.isBefore(endDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showMessage(Component c) {
        JOptionPane.showMessageDialog(c, MESSAGE, "Restricted", JOptionPane.WARNING_MESSAGE);
    }
}
