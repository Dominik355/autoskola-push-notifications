
package com.example.SSEnotifications.service;

import com.example.SSEnotifications.models.EmitterRegistration;
import com.example.SSEnotifications.models.NotificationSave;
import com.example.SSEnotifications.models.PushNotification;
import com.example.SSEnotifications.repositories.EmitterRegistrationRepository;
import com.example.SSEnotifications.repositories.NotificationSaveRepository;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Transactional
public class SseService {
    
    @Autowired
    private EmitterRegistrationRepository emitterRepository;
    
    @Autowired
    private NotificationSaveRepository saveRepository;
    
    private final int HOURS_TO_DELETE = 24;
    
    final HashMap<String, SseEmitter> emitters = new HashMap<>();
    
    
    public void addEmitter(SseEmitter emitter, String email) {
        this.emitters.put(email, emitter);
    }
    
    public void removeEmitter(String email) {
        this.emitters.remove(email);
    }
    
    public HashMap<String, SseEmitter> getEmitters(){
        return emitters;
    }
    
    @Scheduled(fixedDelay = 10000)
    @PostConstruct
    public void sizeEmitters() {
        System.out.println("pocet aktivnych uzivatelov:"+ emitters.size());
    }
    
    public void notifyAll(PushNotification notification) {
        List<String> deadEmitters = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        if(!emitters.isEmpty()) {
            for(Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
                String email = entry.getKey();
                SseEmitter emitter = entry.getValue();
                try{
                    emitter.send(SseEmitter.event()
                            .data(notification.getDate()
                                    +" "+notification.getMessage()));
                    emails.add(email);
                } catch(IOException e ) {
                    System.out.println("Exception occured");
                    deadEmitters.add(email);
                }
            }
            deadEmitters.forEach(email -> {
                setEmitterIsActive(email, false);
                emitters.remove(email);
            });
            if(!notification.getMessage().equals("")) {
                storeForOthers(emails, notification);
            }
        }
    }
    
    
    public ResponseEntity notifySpecificUser(PushNotification notification) {
        String email = notification.getEmail();
        
        if(emitterRepository.existsByUserEmailAndIsActive(email, true)) {
            SseEmitter emitter = emitters.get(email);
            try{
                emitter.send(SseEmitter.event()
                        .data(notification.getDate()
                                +" "+notification.getSchoolName().orElse("")
                                +" "+notification.getMessage()));
                System.out.println("Notify send to: "+email);
                return new ResponseEntity("Notification has been sent", HttpStatus.OK);
            } catch(IOException e) {
                System.out.println("Exception occured");
                storeNotification(notification);
                emitters.remove(email);
                setEmitterIsActive(email, false);
            }
        } else if (emitterRepository.existsByUserEmail(email)) {
            storeNotification(notification);
        }
        return new ResponseEntity("This email is not registered ", HttpStatus.BAD_REQUEST);
    }
    
    
    public SseEmitter getEmitter(String id) throws IOException {
    // ak je dane ID registrovane, zoberie email, emiter zaregistruje a vrati ho 
        if(emitterRepository.existsByGeneratedId(id)){
            String email = emitterRepository.findByGeneratedId(id).getUserEmail();
            if(emitters.containsKey(email)) {
                emitters.remove(email);
            }
            final SseEmitter emitter = new SseEmitter();
            emitter.onCompletion(() -> removeEmitter(email));
            emitter.onTimeout(() -> removeEmitter(email));
            addEmitter(emitter, email);
            emitter.send("You received emitter instance for: "+email);
            setEmitterIsActive(email, true);
            sendStoredNotifications(email);
            return emitter;
        } 
        System.out.println("Takyto email nie je registrovany");
        return null;
    }
    
    //vytvri zaznam a vrati vygenerovane ID
    public String registerUser(String email) {
        //zisti ci uz existuje, ak hej - updatuje ID, ak nie , vytvori novy zaznam
        EmitterRegistration reg;
        String generatedID = generateID();
        if(emitterRepository.existsByUserEmail(email)) {
            reg = emitterRepository
                    .getOne(emitterRepository.findByUserEmail(email).getId());
            reg.setGeneratedID(generatedID);
            reg.setLastOnline(new Timestamp(System.currentTimeMillis()));
            emitterRepository.save(reg);
        } else {
            reg = emitterRepository.save(
                    new EmitterRegistration(email, generatedID));
        }
        return generatedID;
    }
    
    
    public String generateID() {
        int leftLimit = 48; // '0'
        int rightLimit = 122; // 'z'
        int targetStringLength = 20;
        Random random = new Random();
        String generatedString;

        do{
            generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
        } while (emitterRepository.existsByGeneratedId(generatedString));
        return generatedString;
    }
    
    
    public void setEmitterIsActive(String email, boolean isActive) {
        EmitterRegistration em = emitterRepository.getOne(
                emitterRepository.findByUserEmail(email).getId());
        em.setIsActive(isActive);
        emitterRepository.save(em);
    }
    
    
    public String logOut(String email) {
        emitters.remove(email);
        setEmitterIsActive(email, false);
        return "emitter logout Succesfull";
    }
    
    
    public void storeForOthers(List<String> emailsNotified, PushNotification notification) {
        List<String> emailsNotNotified = emitterRepository.findAll().stream()
                    .map(reg -> reg.getUserEmail()).collect(Collectors.toList());
        emailsNotNotified.removeAll(emailsNotified);
        if(!emailsNotNotified.isEmpty()) {
            for(String email : emailsNotNotified) {
            notification.setEmail(email);
            saveRepository.save(new NotificationSave(notification, HOURS_TO_DELETE));
            }
        }
    }
    
    
    public void storeNotification(PushNotification notification) {
        saveRepository.save(new NotificationSave(notification, HOURS_TO_DELETE));
    }
    
    
    public void sendStoredNotifications(String email) {
        List<NotificationSave> saved = saveRepository.findAllByEmail(email);
        List<PushNotification> notifications = saved.stream()
                    .map(PushNotification::new).collect(Collectors.toList());
        saveRepository.deleteAll(saved);
        notifications.sort((o1, o2) -> {
            return o2.getDate().compareTo(o1.getDate());
        });
        notifications.forEach( notify -> notifySpecificUser(notify));
    }
    
}
