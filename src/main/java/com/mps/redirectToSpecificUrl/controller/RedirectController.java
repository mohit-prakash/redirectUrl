package com.mps.redirectToSpecificUrl.controller;

import com.mps.redirectToSpecificUrl.dto.ValidUserDTO;
import com.mps.redirectToSpecificUrl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RedirectController {
    @Autowired
    private UserService userService;
    private static final String ADMIN_UID = "mohitweb";
    private static final String ADMIN_PWD = "Suraj@9031";
    private boolean isAdmin = false;

    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam String username, @RequestParam String password, @RequestParam int chNum) {
        if (userService.validateUser(username, password)) {
            return new RedirectView("http://mohitweb.zapto.org:7878/live/"+chNum+".m3u8");
        } else {
            return new RedirectView("/error");
        }
    }

    @GetMapping("/redirectImg/{imgName}")
    public RedirectView redirectImages(@PathVariable("imgName") String imgName) {
        return new RedirectView("http://mohitweb.zapto.org:7878/jtvimage/"+imgName);
    }

    @GetMapping("/getPlaylist")
    public ResponseEntity<?> fetchM3U(@RequestParam String username,
                                                      @RequestParam String password) {
        try {
            if (userService.validateUser(username, password)) {
                String m3uContent = userService.getPlaylist();
                if (m3uContent != null) {
                    m3uContent = m3uContent.replace("yourUsername", username)
                            .replace("yourPassword", password);
                }
                byte[] m3uBytes = m3uContent.getBytes();
                ByteArrayResource resource = new ByteArrayResource(m3uBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=playlist.m3u");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(m3uBytes.length)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized, check with administrator");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something Wrong: /n"+e.getMessage());
        }
    }
    @GetMapping("/showPlaylist")
    public ResponseEntity<String> showPlaylist(@RequestParam String username, @RequestParam String password) {
        try {
            if (userService.validateUser(username, password)) {
                return ResponseEntity.ok().body("http://mohitweb.zapto.org:7887/getPlaylist?username="+username+"&password="+password);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized, check with administrator");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something Wrong: /n"+e.getMessage());
        }
    }
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshUserCredentials(){
        try {
            userService.loadPlaylist();
            userService.loadCsv();
            return ResponseEntity.ok().body("Refreshed Successfully!!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong!!");
        }
    }
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (ADMIN_UID.equals(username) && ADMIN_PWD.equals(password)){
            isAdmin = true;
            return ResponseEntity.ok().body("Login Success!!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Fail, invalid username or password");
        }
    }
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        isAdmin = false;
        return ResponseEntity.ok().body("Logout Success!!");
    }

    @GetMapping("/addUser")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {
        try {
            if (isAdmin) {
                userService.writeCsv(username+","+password,false);
                return ResponseEntity.ok().body("User Registered Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed to register.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/getList")
    public ResponseEntity<?> getList() {
        try {
            if (isAdmin) {
                List<ValidUserDTO> validUsers = userService.getValidUsers();
                return ResponseEntity.ok().body(validUsers);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed to see list of users");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/removeUser")
    public ResponseEntity<String> removeUser(@RequestParam String username, @RequestParam String password) {
        try {
            if (isAdmin) {
                userService.writeCsv(username+","+password,true);
                return ResponseEntity.ok().body("User access has been revoked.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/error")
    private ResponseEntity<String> error() {
        return new ResponseEntity<String>("You are not authorized!!",HttpStatus.UNAUTHORIZED);
    }
}
