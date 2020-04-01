
package com.example.SSEnotifications.repositories;

import com.example.SSEnotifications.models.NotificationSave;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationSaveRepository extends JpaRepository<NotificationSave, Integer>{
    
    List<NotificationSave> findAllByEmail(String email);
    
}
