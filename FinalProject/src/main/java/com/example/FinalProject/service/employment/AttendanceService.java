package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public byte[] makeQRCode(int width, int height, String url) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG",byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
// =============================================TH=====================================================
    
    // 금일 근무자 리스트 조회
    public List<AdminAttendanceDTO> getTodayAttendances(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        List<Attendance> attendances = attendanceRepository.findTodayAttendances(startOfDay, endOfDay);
        List<AdminAttendanceDTO> dtoList = new ArrayList<>();

        for (Attendance a : attendances) {
            dtoList.add(new AdminAttendanceDTO(
                    a.getSchedule().getContract().getWork().getUser().getUserId(),
                    a.getSchedule().getContract().getWork().getUser().getName(),
                    a.getAttendanceId(),
                    a.getActualStart(),
                    a.getActualEnd(),
                    a.getCommuteStatus(),
                    a.getRemark(),
                    a.getIsNormalAttendance(),
                    a.getTotalMinute()
            ));
        }
        return dtoList;
    }
}
