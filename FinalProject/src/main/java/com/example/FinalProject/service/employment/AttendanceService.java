package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    @Autowired
    public AttendanceService (AttendanceRepository attendanceRepository,CompanyRepository companyRepository){
        this.attendanceRepository = attendanceRepository;
        this.companyRepository = companyRepository;
    }
    public byte[] makeQRCode(Integer companyId) throws WriterException, IOException {
        int width = 200;
        int height = 200;
        Optional<Company> company = companyRepository.findById(companyId);
        if(company.isEmpty()){ return null; }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String url = String.format("http://localhost:8707/checkAttendance?companyId=%d&userId=%s",company.get().getCompanyId(),authentication.getName());
        BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,width,height);
        return new byte[4];
    }
}
