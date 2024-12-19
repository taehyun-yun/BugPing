package com.example.FinalProject.controller.attendance;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.service.employment.AttendanceService;
import com.google.zxing.WriterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class QRController {

    private final AttendanceService attendanceService;
    private final CompanyRepository companyRepository;

    public QRController(AttendanceService attendanceService, CompanyRepository companyRepository){
        this.attendanceService = attendanceService;
        this.companyRepository = companyRepository;
    }
    //출석 체크 QR 생성
    @GetMapping("/makeQR")
    public ResponseEntity<byte[]> makeQR(@RequestParam int companyId) throws WriterException, IOException {
        Optional<Company> company = companyRepository.findById(companyId);
        if(company.isEmpty()){ return null; }
        try{
            //String url = String.format("http://localhost:8707/api/checkAttendance?companyId=%d",company.get().getCompanyId());
            String url = String.format("http://192.168.5.23:8707/checkAttendance?companyId=%d",company.get().getCompanyId());
            byte[] qrCode = attendanceService.makeQRCode(200,200,url);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
        } catch ( Exception e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/checkAttendance")
    public ResponseEntity<String>checkAttendance(@RequestParam Integer companyId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId.equals("anonymousUser")){
            return new ResponseEntity<>("로그인을 해주세요.",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("처리 되었습니다." + LocalDateTime.now() ,HttpStatus.OK);
    }
}
