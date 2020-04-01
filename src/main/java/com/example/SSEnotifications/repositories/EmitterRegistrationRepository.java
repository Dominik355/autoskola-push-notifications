
package com.example.SSEnotifications.repositories;

import com.example.SSEnotifications.models.EmitterRegistration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmitterRegistrationRepository extends JpaRepository<EmitterRegistration, Integer> {
    
    boolean existsByUserEmailAndGeneratedId(String userEmail, String generatedId);
    
    boolean existsByGeneratedId(String generatedId);
    
    boolean existsByUserEmail(String userEmail);
    
    boolean existsByUserEmailAndIsActive(String userEmail, boolean isActive);
    
    EmitterRegistration findByUserEmail(String userEmail);
    
    EmitterRegistration findByGeneratedId(String generatedId);
    
    List<EmitterRegistration> findAllByIsActive(boolean isActive);
    
}
