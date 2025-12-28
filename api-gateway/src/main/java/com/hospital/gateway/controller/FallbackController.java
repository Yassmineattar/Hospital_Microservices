package com.hospital.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller pour gérer les erreurs de circuit breaker
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/patients")
    public ResponseEntity<Map<String, Object>> patientServiceFallback() {
        return createFallbackResponse("Patient Service");
    }

    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> appointmentServiceFallback() {
        return createFallbackResponse("Appointment Service");
    }

    @GetMapping("/bills")
    public ResponseEntity<Map<String, Object>> billingServiceFallback() {
        return createFallbackResponse("Billing Service");
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        return createFallbackResponse("Notification Service");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", serviceName + " is currently unavailable");
        response.put("message", "Please try again later");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
