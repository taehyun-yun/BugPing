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
        List<Object[]> results = attendanceRepository.findSchedulesWithAttendances(todayDayOfWeek);

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
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();

        // Schedule과 Attendance를 LEFT JOIN한 결과 가져오기
        List<Object[]> results = scheduleRepository.findSchedulesWithAttendances(dayOfWeek);

        long totalScheduled = results.size(); // 전체 스케줄 수
        long attended = 0; // 출근한 사람 수
        long notAttended = 0; // 출근하지 않은 사람 수
        long onLeave = 0; // 휴무 상태인 사람 수

        for (Object[] result : results) {
            Schedule schedule = (Schedule) result[0];
            Attendance attendance = result.length > 1 ? (Attendance) result[1] : null;

            if (attendance == null || attendance.getActualStart() == null) {
                notAttended++; // 출근 기록이 없으면 미출근
            } else {
                attended++; // 출근 기록이 있으면 출근한 것으로 간주
            }

            if ("휴무".equals(schedule.getStatus())) {
                onLeave++; // 스케줄 상태가 "휴무"라면 휴무로 간주
            }
        }

        // 출근율 계산
        double attendanceRate = totalScheduled > 0 ? ((double) attended / totalScheduled) * 100 : 0;

        // DTO 반환
        return new AttendanceDetailsDTO(totalScheduled, attended, onLeave, notAttended, attendanceRate);
    }
}
