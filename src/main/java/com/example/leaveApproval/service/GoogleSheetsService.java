package com.example.leaveApproval.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleSheetsService {
    void getSpreadsheetValues() throws IOException, GeneralSecurityException;
    void addSpreadsheetValues(List<Object> dataList) throws IOException, GeneralSecurityException;
}
