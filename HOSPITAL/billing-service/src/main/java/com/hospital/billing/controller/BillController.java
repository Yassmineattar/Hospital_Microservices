package com.hospital.billing.controller;

import com.hospital.billing.model.Bill;
import com.hospital.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable String id) {
        return billingService.getBillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Bill> getBillByAppointmentId(@PathVariable String appointmentId) {
        return billingService.getBillByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Bill>> getBillsByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(billingService.getBillsByPatient(patientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Bill> updateBillStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request
    ) {
        try {
            Bill.BillStatus status = Bill.BillStatus.valueOf(request.get("status"));
            Bill updatedBill = billingService.updateBillStatus(id, status);
            return ResponseEntity.ok(updatedBill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "billing-service"
        ));
    }
}