
package com.example.SSEnotifications.schedulers;

import com.example.SSEnotifications.models.EmitterRegistration;
import com.example.SSEnotifications.models.NotificationSave;
import com.example.SSEnotifications.models.PushNotification;
import com.example.SSEnotifications.repositories.EmitterRegistrationRepository;
import com.example.SSEnotifications.repositories.NotificationSaveRepository;
import com.example.SSEnotifications.service.SseService;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheludingCheck {
    
    private final int HOURS_TO_DELETE = 24;
    
    private final int DAYS_TO_DELETE_USER = 30;
    
    private Logger log = LoggerFactory.getLogger(ScheludingCheck.class);
    
    @Autowired
    private SseService sseService;
    
    @Autowired
    private NotificationSaveRepository notificationRepository;
    
    @Autowired
    private EmitterRegistrationRepository emitterRepository;
    
    //check if users are still online
    @Scheduled(fixedDelay = 600000)
    @PostConstruct
    public void testActivity() {
        log.info("Scheduled test activity started");
        sseService.notifyAll(new PushNotification(""));
    }
      
    
    @Scheduled(fixedDelay = 1800000)
    @PostConstruct
    public void checkNotifications() {
        log.info("Scheduled check messages started");
        //kontroluje, ci nepresla doba expiracie notifikacie, ak hej - vymaze
        List<NotificationSave> notifications = notificationRepository.findAll();
        
        if(!notifications.isEmpty()) {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for(NotificationSave notification : notifications) {
                executor.execute(() -> {
                    Date now = new Timestamp(System.currentTimeMillis());
                    Date notificationValid = notification.getDate();
                    notificationValid.setHours(notificationValid.getHours()+notification.getHoursToDelete());
                    if(notificationValid.before(now)) {
                        notificationRepository.delete(notification);
                    }
                });
            }
            executor.shutdown();
        }        
    }
    
    
    @Scheduled(cron = "0 0 3 ? * * ") // kazdy den o 3 rano
    @PostConstruct
    public void checkUsers() {
        log.info("Scheduled check users registrations started");
        List<EmitterRegistration> users = emitterRepository.findAll();

        if(!users.isEmpty()) {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for(EmitterRegistration user : users) {
                executor.execute(() -> {
                    Date now = new Timestamp(System.currentTimeMillis());
                    Date lastOnline = user.getLastOnline();
                    lastOnline.setDate(lastOnline.getDay()+DAYS_TO_DELETE_USER);
                    if(lastOnline.before(now)) {
                        //viac ako 30 dni neaktivny - vymazat
                        emitterRepository.delete(user);
                    }
                });
            }
            executor.shutdown();
        }
    }
    
    //pokial server nahodu vypaodl - status active mohol ostat true, ajked sa odvtedy uz nikdy neprihlasily,
    //preto pri kazdom nastartovani programu sa da vsetkym hodnota false
    @PostConstruct
    public void changeUsersStatus() {
        emitterRepository.saveAll(emitterRepository
                .findAll().stream()
                .filter(t -> {
                    t.setIsActive(false);
                    return true;
                        }).collect(Collectors.toList()));
    }

}
