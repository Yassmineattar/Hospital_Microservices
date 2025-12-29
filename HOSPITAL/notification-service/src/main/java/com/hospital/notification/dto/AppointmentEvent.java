package com.hospital.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentEvent {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String time;
    private String eventType;
}