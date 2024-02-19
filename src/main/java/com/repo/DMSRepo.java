/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.common.Global;
import com.common.Util1;
import com.dms.model.CVFile;
import com.dms.model.CVFileResponse;
import com.dms.model.ContractDto;
import com.dms.model.FileObject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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

    public String getRootUrl() {
        return System.getProperty("dms.url");
    }

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

    public Mono<List<CVFile>> getTrash() {
        return dmsApi.get()
                .uri(builder -> builder.path("/file/getTrash")
                .build())
                .retrieve()
                .bodyToFlux(CVFile.class)
                .onErrorResume((e) -> {
                    log.error("getTrash : " + e.getMessage());
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

    public Mono<CVFileResponse> createFolder(FileObject obj) {
        obj.setUserId(Global.loginUser.getUserCode());
        return dmsApi.post()
                .uri("/file/createFolder")
                .body(Mono.just(obj), FileObject.class)
                .retrieve()
                .bodyToMono(CVFileResponse.class);

    }

    public Mono<Resource> viewFile(String fileId) {
        return dmsApi.get()
                .uri("file/view/{fileId}", fileId)
                .retrieve()
                .bodyToMono(Resource.class);
    }

    public Mono<CVFile> findFile(String fileId) {
        return dmsApi.get().uri(builder -> builder.path("/file/findFile")
                .queryParam("fileId", fileId).build())
                .retrieve()
                .bodyToMono(CVFile.class);
    }

    public Mono<CVFileResponse> createFile(String parentId, Path filePath) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(filePath));
        return dmsApi.post()
                .uri(uriBuilder -> uriBuilder.path("/file/upload")
                .queryParam("parentId", parentId)
                .queryParam("userId", Global.loginUser.getUserCode())
                .build())
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(CVFileResponse.class);
    }

    public Mono<CVFile> updateFile(CVFile file) {
        return dmsApi.put()
                .uri("/file")
                .body(Mono.just(file), CVFile.class)
                .retrieve()
                .bodyToMono(CVFile.class);
    }

    public Mono<Boolean> deleteForever(String fileId) {
        return dmsApi.delete()
                .uri("file/{fileId}", fileId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Flux<ContractDto> getContract() {
        return dmsApi.get()
                .uri(builder -> builder.path("/contract/getContract")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ContractDto.class)
                .onErrorResume((e) -> {
                    log.error("getContract : " + e.getMessage());
                    return Flux.empty();
                });
    }
}
