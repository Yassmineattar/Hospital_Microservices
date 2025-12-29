package com.hospital.notification.service;

import com.hospital.notification.dto.AppointmentEvent;
import com.hospital.notification.model.Notification;
import com.hospital.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotificationFromAppointment(AppointmentEvent event) {
        log.info("🔔 Création de notification pour RDV: {}", event.getAppointmentId());

        String message = String.format(
                "Nouveau rendez-vous créé le %s à %s",
                event.getDate(),
                event.getTime()
        );

        Notification notification = new Notification(
                event.getAppointmentId(),
                event.getPatientId(),
                event.getDoctorId(),
                message
        );

        Notification savedNotification = notificationRepository.save(notification);
        log.info("✅ Notification créée avec succès: {}", savedNotification.getId());

        return savedNotification;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }

    public Notification createCancellationNotification(AppointmentEvent event) {
        log.info("🔔 Création de notification d'annulation pour RDV: {}", event.getAppointmentId());

        String message = String.format(
                "Rendez-vous annulé - Date: %s à %s",
                event.getDate(),
                event.getTime()
        );

        Notification notification = new Notification(
                event.getAppointmentId(),
                event.getPatientId(),
                event.getDoctorId(),
                message
        );
        notification.setType(Notification.NotificationType.APPOINTMENT_CANCELLED);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("✅ Notification d'annulation créée avec succès: {}", savedNotification.getId());

        return savedNotification;
    }

    public List<Notification> getNotificationsByPatient(String patientId) {
        return notificationRepository.findByPatientId(patientId);
    }

    public List<Notification> getUnreadNotificationsByPatient(String patientId) {
        return notificationRepository.findByPatientIdAndReadFalse(patientId);
    }

    public Notification markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}