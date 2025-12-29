package com.hospital.billing.service;

import com.hospital.billing.dto.AppointmentEvent;
import com.hospital.billing.model.Bill;
import com.hospital.billing.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final BillRepository billRepository;

    public Bill createBillFromAppointment(AppointmentEvent event) {
        log.info("💰 Création de facture pour RDV: {}", event.getAppointmentId());

        // Vérifier si la facture existe déjà
        Optional<Bill> existingBill = billRepository.findByAppointmentId(event.getAppointmentId());
        if (existingBill.isPresent()) {
            log.warn("⚠️ Facture déjà existante pour ce RDV");
            return existingBill.get();
        }

        // Créer une nouvelle facture
        Bill bill = new Bill(
                event.getAppointmentId(),
                event.getPatientId(),
                event.getDoctorId()
        );

        Bill savedBill = billRepository.save(bill);
        log.info("✅ Facture créée avec succès: {}", savedBill.getId());

        return savedBill;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Optional<Bill> getBillById(String id) {
        return billRepository.findById(id);
    }

    public Optional<Bill> getBillByAppointmentId(String appointmentId) {
        return billRepository.findByAppointmentId(appointmentId);
    }

    public Bill updateBillStatus(String id, Bill.BillStatus status) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        bill.setStatus(status);
        return billRepository.save(bill);
    }

    public List<Bill> getBillsByPatient(String patientId) {
        return billRepository.findByPatientId(patientId);
    }

    public void cancelBillByAppointmentId(String appointmentId) {
        log.info("❌ Annulation de facture pour RDV: {}", appointmentId);
        
        Optional<Bill> existingBill = billRepository.findByAppointmentId(appointmentId);
        if (existingBill.isPresent()) {
            Bill bill = existingBill.get();
            bill.setStatus(Bill.BillStatus.CANCELLED);
            billRepository.save(bill);
            log.info("✅ Facture annulée avec succès: {}", bill.getId());
        } else {
            log.warn("⚠️ Aucune facture trouvée pour le RDV: {}", appointmentId);
        }
    }
}