package com.airbnb.services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File service for managing data directory and file operations
 */
public class FileService {
    
    private static final String DATA_DIR = "data";
    
    public static final String USERS_FILE = DATA_DIR + "/users.txt";
    public static final String LISTINGS_FILE = DATA_DIR + "/listings.txt";
    public static final String BOOKINGS_FILE = DATA_DIR + "/bookings.txt";
    public static final String REVIEWS_FILE = DATA_DIR + "/reviews.txt";
    public static final String MESSAGES_FILE = DATA_DIR + "/messages.txt";
    
    /**
     * Initialize data directory and create files if they don't exist
     */
    public static void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            // Create files if they don't exist
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(LISTINGS_FILE);
            createFileIfNotExists(BOOKINGS_FILE);
            createFileIfNotExists(REVIEWS_FILE);
            createFileIfNotExists(MESSAGES_FILE);
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
        }
    }
    
    /**
     * Create a file if it doesn't exist
     */
    private static void createFileIfNotExists(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating file " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Read all lines from a file
     */
    public static java.util.List<String> readAllLines(String filePath) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
        return lines;
    }
    
    /**
     * Write a line to a file (append mode)
     */
    public static void appendLine(String filePath, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Write all lines to a file (overwrite mode)
     */
    public static void writeAllLines(String filePath, java.util.List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Delete a line from a file by ID (first field)
     */
    public static boolean deleteLineById(String filePath, String id) {
        java.util.List<String> lines = readAllLines(filePath);
        java.util.List<String> updatedLines = new java.util.ArrayList<>();
        boolean found = false;
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > 0 && !parts[0].equals(id)) {
                updatedLines.add(line);
            } else {
                found = true;
            }
        }
        
        if (found) {
            writeAllLines(filePath, updatedLines);
        }
        return found;
    }
    
    /**
     * Update a line in a file by ID
     */
    public static boolean updateLineById(String filePath, String id, String newLine) {
        java.util.List<String> lines = readAllLines(filePath);
        java.util.List<String> updatedLines = new java.util.ArrayList<>();
        boolean found = false;
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > 0 && parts[0].equals(id)) {
                updatedLines.add(newLine);
                found = true;
            } else {
                updatedLines.add(line);
            }
        }
        
        if (found) {
            writeAllLines(filePath, updatedLines);
        }
        return found;
    }
}

