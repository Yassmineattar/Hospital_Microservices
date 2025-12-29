package com.hospital.notification.consumer;

import com.hospital.notification.config.RabbitMQConfig;
import com.hospital.notification.dto.AppointmentEvent;
import com.hospital.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleAppointmentCreated(AppointmentEvent event) {
        log.info("📩 Événement reçu dans notification-service: {}", event);
        log.info("Type d'événement: {}", event.getEventType());

        try {
            if ("APPOINTMENT_BOOKED".equals(event.getEventType())) {
                notificationService.createNotificationFromAppointment(event);
                log.info("✅ Notification de création traitée avec succès");
            } else if ("APPOINTMENT_CANCELLED".equals(event.getEventType())) {
                notificationService.createCancellationNotification(event);
                log.info("✅ Notification d'annulation traitée avec succès");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement de la notification: {}", e.getMessage());
            throw e;
        }
    }
}