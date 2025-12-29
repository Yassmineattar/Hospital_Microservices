package com.hospital.billing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bills")
public class Bill {

    @Id
    private String id;

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private Double amount;
    private BillStatus status;
    private LocalDateTime createdAt;

    public enum BillStatus {
        PENDING, PAID, CANCELLED
    }

    // Constructeur personnalisé
    public Bill(String appointmentId, String patientId, String doctorId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.amount = 50.0; // Montant par défaut
        this.status = BillStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}