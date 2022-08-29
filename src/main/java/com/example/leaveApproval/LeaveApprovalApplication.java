package com.example.leaveApproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootApplication
@EnableZeebeClient
public class LeaveApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaveApprovalApplication.class, args);
	}

}
