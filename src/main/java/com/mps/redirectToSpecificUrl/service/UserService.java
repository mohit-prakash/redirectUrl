package com.mps.redirectToSpecificUrl.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {
    private String csv = "";
    private String playlist = "";
    public boolean validateUser(String username, String password) {
        if ("".equals(csv)) {
            loadCsv();
        }
        return checkIfExistsInCSV(csv,username+","+password);
    }
    public void loadCsv() {
//        Path path = Paths.get("src/main/resources/validUser.csv");
        Path path = Paths.get("validUser.csv");
        try {
            csv = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    public void loadPlaylist() {
//        Path path = Paths.get("src/main/resources/playlist.m3u");
        Path path = Paths.get("playlist.m3u");
        try {
            playlist = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    public String getPlaylist() {
        if ("".equals(playlist)){
            loadPlaylist();
        }
        return playlist;
    }
    private boolean checkIfExistsInCSV(String csvData, String target) {
        String[] lines = csvData.split("\n");
        for (String line : lines) {
            if (line.trim().equals(target)) {
                return true;
            }
        }
        return false;
    }
}