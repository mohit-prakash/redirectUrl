package com.mps.redirectToSpecificUrl.service;

import com.mps.redirectToSpecificUrl.dto.ValidUserDTO;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public List<ValidUserDTO> getValidUsers() {
        if ("".equals(csv)) {
            loadCsv();
        }
        String[] validUsers = csv.split("\n");
        List<ValidUserDTO> validUserDTOList = new ArrayList<ValidUserDTO>();
        for (String validUser : validUsers) {
            int commaIndex  = validUser.indexOf(",");
            ValidUserDTO validUserDTO = new ValidUserDTO();
            validUserDTO.setUsername(validUser.substring(0, commaIndex));
            validUserDTO.setPassword(validUser.substring(commaIndex+1));
            validUserDTOList.add(validUserDTO);
        }
        return validUserDTOList;
    }

    public void writeCsv(String data,boolean revokedAccess) throws IOException {
        try {
//            Path path = Paths.get("src/main/resources/validUser.csv");
            Path path = Paths.get("validUser.csv");
            csv = Files.readString(path);
            if (revokedAccess) {
                if ("".equals(csv)) {
                    loadCsv();
                }
                boolean isUserExist = checkIfExistsInCSV(csv, data);
                if (isUserExist) {
                    csv = csv.replace(data, "#" + data);
                }
            } else {
                if ("".equals(csv)) {
                    loadCsv();
                }
                boolean isUserExist = checkIfExistsInCSV(csv, "#"+data);
                if (isUserExist) {
                    csv = csv.replace("#" + data, data);
                } else {
                    csv = csv + "\n" + data;
                }
            }
            Files.writeString(path, csv);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
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