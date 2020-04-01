
package com.example.SSEnotifications.controller;

import com.example.SSEnotifications.models.PushNotification;
import com.example.SSEnotifications.service.SseService;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class SseController {
    
    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss ");
    
    @Autowired
    private SseService sseService;
    
    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    
    
    @GetMapping(value = {"/notification/{id}"})
    public ResponseEntity<SseEmitter> doNotify(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        //kvoli firefoxu, aby prijimal viackrat z rovnakeho zdroja
        response.addHeader("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>(sseService.getEmitter(id), HttpStatus.OK);
    }
    
    
    @GetMapping(value = {"/getEmitterID/{userEmail}"})
    public ResponseEntity getEmitterID(@PathVariable("userEmail") String userEmail) {
        return new ResponseEntity(sseService.registerUser(userEmail), HttpStatus.OK);
    }
    
    
    @PostMapping(value = {"/postMessage"})
    public ResponseEntity postMessage(@RequestBody PushNotification notification) {
        if(notification.getEmail().length()<5){
            notification.setDate(new Timestamp(System.currentTimeMillis()));
            sseService.notifyAll(notification);
            return new ResponseEntity("Notifications has been sent", HttpStatus.OK);
        }
        return sseService.notifySpecificUser(notification);
    }

    @GetMapping(value = {"/logOut/{userEmail}"})
    public ResponseEntity logOutUser(@PathVariable("userEmail") String userEmail) {
        return new ResponseEntity(sseService.logOut(userEmail), HttpStatus.OK);
    }
    
}
