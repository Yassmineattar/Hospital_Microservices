package com.hospital.notification.repository;

import com.hospital.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByPatientId(String patientId);

    List<Notification> findByPatientIdAndReadFalse(String patientId);

    List<Notification> findByAppointmentId(String appointmentId);
}