package com.hospital.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;

    public enum NotificationType {
        APPOINTMENT_CREATED,
        APPOINTMENT_CANCELLED,
        REMINDER
    }

    // Constructeur personnalisé
    public Notification(String appointmentId, String patientId,
                        String doctorId, String message) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.message = message;
        this.type = NotificationType.APPOINTMENT_CREATED;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }
}