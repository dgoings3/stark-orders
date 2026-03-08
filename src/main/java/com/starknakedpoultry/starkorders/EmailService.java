package com.starknakedpoultry.starkorders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.to}")
    private String toEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNewOrderEmail(Order order) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Stark Naked Poultry Order");

        String body =
                "New order received\n\n" +
                "Name: " + order.getCustomerName() + "\n" +
                "Phone: " + order.getPhone() + "\n" +
                "Quantity: " + order.getQuantity() + "\n" +
                "Pickup Date: " + order.getFormattedPickupDate() + "\n" +
                "Pickup Time: " + order.getFormattedPickupTime() + "\n" +
                "Source: " + order.getSource() + "\n" +
                "Status: " + order.getStatus() + "\n" +
                (order.hasNotes() ? "\nNotes: " + order.getNotes() : "");

        message.setText(body);

        mailSender.send(message);

        System.out.println("EMAIL SENT: New order from " + order.getCustomerName());
    }

    public void sendManualReservationEmail(Order order) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Manual Reservation Added");

        String body =
                "A manual reservation was added\n\n" +
                "Name: " + order.getCustomerName() + "\n" +
                "Quantity: " + order.getQuantity() + "\n" +
                "Pickup Date: " + order.getPickupDate() + "\n" +
                "Pickup Time: " + order.getPickupTime() + "\n" +
                "Source: " + order.getSource() + "\n" +
                "Status: " + order.getStatus();

        message.setText(body);

        mailSender.send(message);

        System.out.println("EMAIL SENT: Manual reservation for " + order.getCustomerName());
    }
}