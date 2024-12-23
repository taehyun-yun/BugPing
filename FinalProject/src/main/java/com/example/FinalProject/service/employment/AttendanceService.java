package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.AttendanceDetailsDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public AttendanceService (AttendanceRepository attendanceRepository,CompanyRepository companyRepository, ScheduleRepository scheduleRepository){
        this.attendanceRepository = attendanceRepository;
        this.companyRepository = companyRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public byte[] makeQRCode(int width, int height, String url) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG",byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

// =============================================TH=====================================================

    // 금일 근무자 리스트 조회
    public List<AdminAttendanceDTO> getTodayAttendances() {
        int todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // 월요일: 1 ~ 일요일: 7
        LocalDate todayDate = LocalDate.now(); // 오늘 날짜

        // 오늘 날짜와 요일을 기준으로 스케쥴 및 출근 데이터를 조회
        List<Object[]> results = attendanceRepository.findSchedulesWithAttendances(todayDayOfWeek, todayDate);

        List<AdminAttendanceDTO> dtoList = new ArrayList<>();
        for (Object[] result : results) {
            Schedule schedule = (Schedule) result[0];
            Attendance attendance = (Attendance) result[1]; // LEFT JOIN 결과가 없을 경우 null

            dtoList.add(new AdminAttendanceDTO(
                    schedule.getContract().getWork().getUser().getUserId(),
                    schedule.getContract().getWork().getUser().getName(),
                    (attendance != null ? attendance.getAttendanceId() : null),
                    (attendance != null ? attendance.getActualStart() : null),
                    (attendance != null ? attendance.getActualEnd() : null),
                    (attendance != null ? attendance.getCommuteStatus() : "미출근"),
                    (attendance != null ? attendance.getRemark() : null),
                    (attendance != null ? attendance.getIsNormalAttendance() : "N"),
                    (attendance != null ? attendance.getTotalMinute() : 0)
            ));
        }

        return dtoList;
    }

    public AttendanceDetailsDTO getTodayScheduleBasedStatistics() {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        int dayOfWeek = today.getDayOfWeek().getValue(); // 요일 (월요일: 1, 일요일: 7)
        LocalDateTime startOfDay = today.atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endOfDay = today.atTime(23, 59, 59); // 오늘 23:59:59

        // Schedule과 Attendance를 LEFT JOIN한 결과 가져오기
        List<Object[]> results = scheduleRepository.findSchedulesWithAttendances(dayOfWeek, startOfDay, endOfDay);


        long totalScheduled = 0; // 전체 스케줄 수
        long attended = 0; // 출근한 사람 수
        long onLeave = 0; // 휴무 상태인 사람 수
        long notYetStarted = 0; // 출근 전인 사람 수

        for (Object[] result : results) {
            Schedule schedule = (Schedule) result[0];
            Attendance attendance = (Attendance) result[1]; // Attendance 데이터가 없을 수 있음 (LEFT JOIN)

            System.out.println("Schedule day: " + schedule.getDay() + ", Today day: " + dayOfWeek);

            if (schedule.getDay() == dayOfWeek) { // 금일 스케줄 여부 확인
                totalScheduled++; // 금일 스케줄에 해당하면 카운트 증가
                if (attendance == null || attendance.getActualStart() == null) {
                    // 출근 기록이 없으면 출근 전 상태로 처리
                    notYetStarted++;
                } else {
                    // 출근 기록이 있으면 출근으로 처리
                    attended++;
                }
            } else {
                // 금일 스케줄이 아니면 휴무로 간주
                onLeave++;
            }
        }
        System.out.println("휴무자 : " + onLeave);
        // 출근율 계산
        double attendanceRate = totalScheduled > 0 ? ((double) attended / totalScheduled) * 100 : 0;

        // DTO 생성 및 반환
        return new AttendanceDetailsDTO(totalScheduled, attended, onLeave, notYetStarted, attendanceRate);
    }
}
