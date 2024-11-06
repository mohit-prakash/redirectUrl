package com.mps.redirectToSpecificUrl.controller;

import com.mps.redirectToSpecificUrl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RedirectController {
    @Autowired
    private UserService userService;

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
    @GetMapping("/error")
    private ResponseEntity<String> error() {
        return new ResponseEntity<String>("You are not authorized!!",HttpStatus.UNAUTHORIZED);
    }
}
