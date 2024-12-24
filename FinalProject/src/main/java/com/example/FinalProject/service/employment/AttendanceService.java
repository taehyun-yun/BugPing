package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.AttendanceDetailsDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.work.WorkRepository;
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
import java.util.*;

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

    // 금일 근무자 리스트 조회
    public List<AdminAttendanceDTO> getTodayAttendances(Integer companyId) {
        int todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // 월요일: 1 ~ 일요일: 7
        //LocalDate todayDate = LocalDate.now(); // 오늘 날짜

        // 오늘 날짜와 요일을 기준으로 스케쥴 및 출근 데이터를 조회
        List<Object[]> results = attendanceRepository.findSchedulesWithAttendances(companyId, todayDayOfWeek);

        // 중복 제거를 위한 Map 사용 (Key: userId, Value: DTO)
        Map<String, AdminAttendanceDTO> dtoMap = new LinkedHashMap<>();

        for (Object[] result : results) {
            Schedule schedule = (Schedule) result[0];
            Attendance attendance = (Attendance) result[1]; // LEFT JOIN 결과가 없을 경우 null
            String userId = schedule.getContract().getWork().getUser().getUserId();

            // 중복된 사용자 제거
            if (!dtoMap.containsKey(userId)) {
                AdminAttendanceDTO dto = new AdminAttendanceDTO(
                        userId,
                        schedule.getContract().getWork().getUser().getName(),
                        attendance != null ? attendance.getAttendanceId() : null,
                        attendance != null ? attendance.getActualStart() : null,
                        attendance != null ? attendance.getActualEnd() : null,
                        attendance != null ? attendance.getCommuteStatus() : "미출근",
                        attendance != null ? attendance.getRemark() : null,
                        attendance != null ? attendance.getIsNormalAttendance() : "N",
                        attendance != null ? attendance.getTotalMinute() : 0
                );
                dtoMap.put(userId, dto);
            }
        }

        // Map의 값들을 리스트로 변환하여 반환
        return new ArrayList<>(dtoMap.values());
    }

    public AttendanceDetailsDTO getTodayScheduleBasedStatistics(Integer companyId) {
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();

        // DB에서 데이터를 가져오기
        List<Object[]> results = scheduleRepository.findSchedulesWithAttendances(companyId, dayOfWeek);

        // 변수 초기화
        Set<String> scheduledUserIds = new HashSet<>();
        long totalScheduled = 0;
        long attended = 0;
        long notYetStarted = 0;

        // 스케줄된 사용자 계산
        for (Object[] result : results) {
            Schedule schedule = (Schedule) result[0];
            Attendance attendance = (Attendance) result[1];
            String userId = schedule.getContract().getWork().getUser().getUserId();

            // 중복 제거
            if (scheduledUserIds.add(userId)) {
                totalScheduled++;
                if (attendance == null || attendance.getActualStart() == null) {
                    notYetStarted++;
                } else {
                    attended++;
                }
            }
        }

        // 전체 사용자 ID 목록 가져오기
        List<String> allUserIds = workRepository.findAllUserIdsByCompanyId(companyId);

        // 관리자 제외
        allUserIds.remove("master");

        // 휴무 계산
        long onLeave = allUserIds.size() - scheduledUserIds.size();

        // 출근율 계산
        double attendanceRate = totalScheduled > 0 ? ((double) attended / totalScheduled) * 100 : 0;

        return new AttendanceDetailsDTO(totalScheduled, attended, onLeave, notYetStarted, attendanceRate);
    }
}
