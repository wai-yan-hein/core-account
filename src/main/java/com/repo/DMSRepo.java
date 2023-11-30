/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.IconManager;
import com.common.Util1;
import com.dms.model.CVFile;
import com.dms.model.FileObject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class DMSRepo {

    @Autowired
    private WebClient dmsApi;

    public Mono<List<CVFile>> getFolder(String parentId) {
        return dmsApi.get()
                .uri(builder -> builder.path("/file/getFolder")
                .queryParam("parentId", parentId)
                .build())
                .retrieve()
                .bodyToFlux(CVFile.class)
                .onErrorResume((e) -> {
                    log.error("getFolder : " + e.getMessage());
                    return Mono.empty();
                }).collectList();
    }

    public Mono<List<CVFile>> getFile(String parentId) {
        return dmsApi.get()
                .uri(builder -> builder.path("/file/getFile")
                .queryParam("parentId", parentId)
                .build())
                .retrieve()
                .bodyToFlux(CVFile.class)
                .onErrorResume((e) -> {
                    log.error("getFile : " + e.getMessage());
                    return Mono.empty();
                }).collectList();
    }

    public Mono<FileObject> getStorageInfo() {
        return dmsApi.get()
                .uri(builder -> builder.path("/file/getStorageInfo")
                .build())
                .retrieve()
                .bodyToMono(FileObject.class);
    }

    public Mono<ImageIcon> getIcon(String extension) {
        if (!Util1.isNullOrEmpty(extension)) {
            return dmsApi.get()
                    .uri(builder -> builder.path("/icons/" + extension)
                    .build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .map(this::generateIcon);
        }
        return Mono.empty();
    }

    private ImageIcon generateIcon(byte[] imageData) {
        try {
            // Create BufferedImage from byte array
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            // Create ImageIcon from BufferedImage
            return new ImageIcon(bufferedImage);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null; // Handle the exception as needed
        }
    }
}
