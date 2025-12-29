package com.hospital.billing.consumer;

import com.hospital.billing.config.RabbitMQConfig;
import com.hospital.billing.dto.AppointmentEvent;
import com.hospital.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    private final BillingService billingService;

    @RabbitListener(queues = RabbitMQConfig.BILLING_QUEUE)
    public void handleAppointmentCreated(AppointmentEvent event) {
        log.info("📩 Événement reçu dans billing-service: {}", event);
        log.info("Type d'événement: {}", event.getEventType());

        try {
            if ("APPOINTMENT_BOOKED".equals(event.getEventType())) {
                billingService.createBillFromAppointment(event);
                log.info("✅ Facture créée avec succès");
            } else if ("APPOINTMENT_CANCELLED".equals(event.getEventType())) {
                billingService.cancelBillByAppointmentId(event.getAppointmentId());
                log.info("✅ Facture annulée avec succès");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement de la facture: {}", e.getMessage());
            throw e; // Relancer pour que RabbitMQ puisse retry
        }
    }
}