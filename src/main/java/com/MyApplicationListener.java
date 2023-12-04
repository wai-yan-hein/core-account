package com;

import com.common.Util1;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyApplicationListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // Add cleanup or logging logic here
        log.info("Application is exiting. Do cleanup or logging here.");
        updateProgram();
    }

    private void updateProgram() {
        try {
            String updateFile = "update/core-account.jar";
            String currentFile = "core-account.jar";
            Util1.updateFile(updateFile, currentFile);
        } catch (IOException ex) {
            log.error("updateProgram : " + ex.getMessage());
        }

    }
}
