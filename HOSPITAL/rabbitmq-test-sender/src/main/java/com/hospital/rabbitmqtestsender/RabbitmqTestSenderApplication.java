package com.hospital.rabbitmqtestsender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class RabbitmqTestSenderApplication {

    private static final String EXCHANGE_NAME = "hospital_events";
    private static final String ROUTING_KEY = "appointment.created";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

            Map<String, String> event = new HashMap<>();
            event.put("appointmentId", "APT-" + System.currentTimeMillis());
            event.put("patientId", "PAT-001");
            event.put("doctorId", "DOC-001");
            event.put("date", "2025-12-10");
            event.put("time", "14:00");

            ObjectMapper mapper = new ObjectMapper();
            String jsonEvent = mapper.writeValueAsString(event);

            // Add the required headers for Spring AMQP
            Map<String, Object> headers = new HashMap<>();
            headers.put("__TypeId__", "com.hospital.billing.dto.AppointmentEvent");

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .contentEncoding("UTF-8")  // Add encoding
                    .deliveryMode(2)
                    .priority(0)
                    .headers(headers)  // Add the type header
                    .build();

            channel.basicPublish(
                    EXCHANGE_NAME,

                    ROUTING_KEY,
                    props,
                    jsonEvent.getBytes("UTF-8")  // Specify encoding
            );

            System.out.println("✅ Événement envoyé : " + jsonEvent);

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}