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
    public static final String MESSAGE = "Access to certain data is now restricted by management.";

    public void initLockDate() {
        log.info("initLockDate.");
        userRepo.getDateLock().subscribe((t) -> {
            t.removeIf(c -> !c.isDateLock());
            listLock = t;
        });
    }

    public static boolean isLockDate(Date date) {
        if (listLock != null) {
            for (DateLock dateLock : listLock) {
                Date startDate = dateLock.getStartDate();
                Date endDate = dateLock.getEndDate();
                if ((date.equals(startDate) || date.equals(endDate))
                        || (date.after(startDate) && date.before(endDate))) {
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
                LocalDate startDate = Util1.convertToLocalDateTime(dateLock.getStartDate()).toLocalDate();
                LocalDate endDate = Util1.convertToLocalDateTime(dateLock.getEndDate()).toLocalDate();
                if ((userDate.isEqual(startDate) || userDate.isEqual(endDate))
                        || (userDate.isAfter(startDate) && userDate.isBefore(endDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showMessage(Component c) {
        JOptionPane.showMessageDialog(c, "Your entry date has been restricted by management.", "Restricted", JOptionPane.WARNING_MESSAGE);
    }
}
