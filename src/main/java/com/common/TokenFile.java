package com.common;

import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@Slf4j
public class TokenFile<T> {

    private final String filePath = "token/";
    private final String fileName = "secret.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Class<T> persistentClass;

    public TokenFile(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public void write(T obj) {
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(obj, writer);
        } catch (IOException ex) {
            log.error("Failed to save: " + ex.getMessage());
        }
    }

    public T read() {
        File file = new File(filePath, fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            return gson.fromJson(jsonBuilder.toString(), persistentClass);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Token not found. Check Internert Connection.","Access Denied",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            log.error("Failed to read: " + e.getMessage());
        }
        return null;
    }
}
