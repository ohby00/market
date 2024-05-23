package com.osio.userservice.service;

public interface EmailService {
    void send(String to, String subject, String text);
}
