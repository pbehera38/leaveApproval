package com.example.leaveApproval.service;

import com.example.leaveApproval.dto.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
}