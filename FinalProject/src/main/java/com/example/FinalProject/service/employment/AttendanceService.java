package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.DailyAttendanceDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final ScheduleRepository scheduleRepository;
    private final WorkRepository workRepository;

    @Autowired
    public AttendanceService (AttendanceRepository attendanceRepository,CompanyRepository companyRepository, ScheduleRepository scheduleRepository, WorkRepository workRepository){
        this.attendanceRepository = attendanceRepository;
        this.companyRepository = companyRepository;
        this.scheduleRepository = scheduleRepository;
        this.workRepository = workRepository;
    }

    public byte[] makeQRCode(int width, int height, String url) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG",byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

// =============================================TH=====================================================

    // 스케줄 DB 금일 출근자 리스트 조회.
    public List<AdminAttendanceDTO> getTodaySchedules(Integer companyId) {
        // 현재 날짜와 요일
        LocalDateTime currentDateTime = LocalDateTime.now();
        int todayDayOfWeek = currentDateTime.getDayOfWeek().getValue();

        log.info("Today DateTime: {}, Day of Week: {}", currentDateTime, todayDayOfWeek);

        // 스케줄 데이터 조회
        List<Schedule> schedules = scheduleRepository.findSchedulesByCompanyAndDay(companyId, todayDayOfWeek);

        // DTO 생성
        List<AdminAttendanceDTO> scheduleList = new ArrayList<>();
        for (Schedule schedule : schedules) {
            AdminAttendanceDTO dto = new AdminAttendanceDTO(
                    schedule.getContract().getWork().getUser().getUserId(),
                    schedule.getContract().getWork().getUser().getName(),
                    schedule.getOfficialStart(),
                    schedule.getOfficialEnd()
            );

            scheduleList.add(dto);
        }

        log.info("Schedules found: {}", scheduleList);
        return scheduleList;
    }

    // 오늘 날짜의 attendance 데이터만 가져오기
    public List<DailyAttendanceDTO> getTodayAttendanceData(Integer companyId) {
        // 오늘 날짜의 출석 데이터를 조회
        List<Attendance> attendances = attendanceRepository.findTodayAttendances(companyId);

        // Attendance 데이터를 DTO로 변환
        return attendances.stream().map(attendance -> new DailyAttendanceDTO(
                attendance.getSchedule().getContract().getWork().getUser().getUserId(),
                attendance.getSchedule().getContract().getWork().getUser().getName(),
                attendance.getAttendanceId(),
                attendance.getActualStart(),
                attendance.getActualEnd(),
                attendance.getCommuteStatus(),
                attendance.getRemark(),
                attendance.getIsNormalAttendance(),
                attendance.getTotalMinute()
        )).collect(Collectors.toList());
    }

}
