package com.example.leaveApproval.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.leaveApproval.dto.EmailDetails;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class JobWorker {
	
	private static final Logger LOG = LoggerFactory.getLogger(JobWorker.class);
	
	@Autowired
    private GoogleSheetsService googleSheetsService;
	
	@Autowired
    private EmailService emailService;
	
	@ZeebeWorker(type = "saveRequest", autoComplete = true)
	public void saveRequest(final JobClient client, final ActivatedJob job) {
		Map<String, Object> variables = job.getVariablesAsMap();
		List<Object> dataList = new ArrayList<>();
		dataList.add(variables.get("requesterEmail"));
		dataList.add(variables.get("managerEmail"));
		dataList.add(variables.get("requestedFrom"));
		dataList.add(variables.get("requestedTo"));
		dataList.add(variables.get("comment"));
		dataList.add(variables.get("managerComment"));
		try {
			googleSheetsService.addSpreadsheetValues(dataList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("Process variables: "+variables);
	}
	
	@ZeebeWorker(type = "sendEmail", autoComplete = true)
	public void sendEmail(final JobClient client, final ActivatedJob job) {
		Map<String, Object> variables = job.getVariablesAsMap();
		LOG.info("Process variables sendEmail: "+variables);
		emailService.sendSimpleMail(prepareMail(variables));
	}
	
	private EmailDetails prepareMail(Map<String, Object> variables) {
		EmailDetails details = new EmailDetails();
		if(variables.containsKey("action") && "Rejected".equalsIgnoreCase(variables.get("action").toString())) {
			details.setRecipient(variables.get("requesterEmail").toString());
			details.setSubject("Leave Request Rejected");
			StringBuilder msgBody = new StringBuilder();
			msgBody.append("Dear ");
			msgBody.append(variables.get("requesterEmail").toString());
			msgBody.append(", ");
			msgBody.append(variables.get("managerEmail").toString());
			msgBody.append(" rejected your leave request. Reason is ");
			msgBody.append(variables.get("managerComment"));
			details.setMsgBody(msgBody.toString());
		} else if(variables.containsKey("action") && "Approved".equalsIgnoreCase(variables.get("action").toString())) {
			
			details.setRecipient(variables.get("requesterEmail").toString());
			details.setSubject("Leave Request Approved");
			StringBuilder msgBody = new StringBuilder();
			msgBody.append("Dear ");
			msgBody.append(variables.get("requesterEmail").toString());
			msgBody.append(", ");
			msgBody.append(variables.get("managerEmail").toString());
			msgBody.append(" approved your leave request.");
			details.setMsgBody(msgBody.toString());
		}
		return details;
		
	}


}
