package com.hospital.billing.repository;

import com.hospital.billing.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {

    Optional<Bill> findByAppointmentId(String appointmentId);

    List<Bill> findByPatientId(String patientId);

    List<Bill> findByStatus(Bill.BillStatus status);
}