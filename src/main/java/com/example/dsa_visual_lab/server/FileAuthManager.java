package com.example.dsa_visual_lab.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

public class FileAuthManager {

    private static final String FILE_PATH = "users.txt";

    public FileAuthManager() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating user file: " + e.getMessage());
            }
        }
    }

    public boolean registerUser(String username, String password) {
        if (userExists(username)) return false;

        String hashedPassword = hashPassword(password);
        String record = username + "," + hashedPassword + System.lineSeparator();

        try {
            Files.write(Paths.get(FILE_PATH), record.getBytes(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            String hashedInput = hashPassword(password);

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(hashedInput)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        }
        return false;
    }

    private boolean userExists(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}