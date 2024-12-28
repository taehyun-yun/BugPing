package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.AttendanceDetailsDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final ScheduleRepository scheduleRepository;
    private final WorkChangeRepository workChangeRepository;

    @Autowired
    public AttendanceService (AttendanceRepository attendanceRepository,CompanyRepository companyRepository, ScheduleRepository scheduleRepository, WorkChangeRepository workChangeRepository){
        this.attendanceRepository = attendanceRepository;
        this.companyRepository = companyRepository;
        this.scheduleRepository = scheduleRepository;
        this.workChangeRepository = workChangeRepository;
    }
    //QR 만들기
    public byte[] makeQRCode(int width, int height, String url) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG",byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    //일정 불러오기
    public List<Schedule> getOneSchedule(String userId, Integer companyId) {
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        List<Schedule>schedules = scheduleRepository.findOneSchedules(userId, companyId, tomorrow, today);
        return schedules == null ? Collections.emptyList() : schedules;
    }
    public List<WorkChange> getOneWorkChange(String userId, Integer companyId){
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        List<WorkChange>workChanges = workChangeRepository.findOneWorkChange(userId,companyId,tomorrow,today);
        return workChanges == null? Collections.emptyList() : workChanges;
    }
    //스케쥴 출근
    public boolean commuteCheckByScheduleId(Integer scheduleId){
        Optional<Schedule> isSchedule = scheduleRepository.findById(scheduleId);
        if(isSchedule.isEmpty()){
            return false;
        }
        Schedule schedule = isSchedule.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDay = schedule.getOfficialStart().isBefore(schedule.getOfficialEnd()) ? now : now.minusDays(1) ;

        Attendance attendance = new Attendance();
        attendance.setActualStart(now);
        attendance.setSchedule(schedule);
        attendance.setStatus("T");
        attendance.setRecognizedWorkStart(LocalDateTime.of(now.getYear(),now.getMonth().getValue(),now.getDayOfMonth(),schedule.getOfficialStart().getHour(),schedule.getOfficialStart().getMinute()));
        attendance.setRecognizedWorkEnd(LocalDateTime.of(endDay.getYear(),endDay.getMonth().getValue(),endDay.getDayOfMonth(),schedule.getOfficialEnd().getHour(),schedule.getOfficialEnd().getMinute()));
        attendance.setRecognizedWorkBreakMinute(schedule.getBreakMinute());
        //지각하든말든 일단 기본 계약한 스케쥴 대로 인정
        Duration duration = Duration.between(schedule.getOfficialStart(),schedule.getOfficialEnd());
        int minute = (int) duration.toMinutes();
        int breaktime = 30 * (minute / 240);
        attendance.setRecognizedWorkMinute(minute - breaktime);
        attendance.setTotalMinute(minute - breaktime);

        //지각에따라
        String commuteStatus;
        String isNormalAttendance;

        if(schedule.getOfficialStart().isAfter(now.toLocalTime())){
            commuteStatus =  "정상";
            isNormalAttendance = "Y";
        } else {
            commuteStatus = "지각";
            isNormalAttendance = "N";
        }
        attendance.setCommuteStatus(commuteStatus);
        attendance.setIsNormalAttendance(isNormalAttendance);

        attendanceRepository.save(attendance);
        return true;
    }
    //변경 출근
    public boolean commuteCheckByWorkChangeId(Integer workChangeId){
        Optional<WorkChange> isWorkChange = workChangeRepository.findById(workChangeId);
        if(isWorkChange.isEmpty()){
            return false;
        }
        WorkChange workChange = isWorkChange.get();
        Schedule schedule = workChange.getSchedule();
        LocalDateTime now = LocalDateTime.now();
        //LocalDateTime endDay = schedule.getOfficialStart().isBefore(schedule.getOfficialEnd()) ? now : now.minusDays(1) ;
        Attendance attendance = new Attendance();

        attendance.setActualStart(now);
        attendance.setSchedule(schedule);
        attendance.setStatus("T");
        attendance.setRecognizedWorkStart(workChange.getChangeStartTime());
        attendance.setRecognizedWorkEnd(workChange.getChangeEndTime());
        Duration duration = Duration.between(workChange.getChangeStartTime(),workChange.getChangeEndTime());
        int minute = (int) duration.toMinutes();
        int breaktime = 30 * (minute / 240);
        attendance.setRecognizedWorkMinute(minute - breaktime);
        attendance.setTotalMinute(minute- 30 * breaktime);

        //지각에따라
        String commuteStatus;
        String isNormalAttendance;

        if(schedule.getOfficialStart().isAfter(now.toLocalTime())){
            commuteStatus =  "정상";
            isNormalAttendance = "Y";
        } else {
            commuteStatus = "지각";
            isNormalAttendance = "N";
        }
        attendance.setCommuteStatus(commuteStatus);
        attendance.setIsNormalAttendance(isNormalAttendance);

        attendanceRepository.save(attendance);
        return true;
    }
    //중복 출근 체크
    public Attendance already(Integer id, String type){
        if(type.equals("scheduleId")){
            Optional<Schedule> isSchedule = scheduleRepository.findById(id);
            if(isSchedule.isEmpty()){
                return null;
            }
            Schedule schedule = isSchedule.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDay = schedule.getOfficialStart().isBefore(schedule.getOfficialEnd()) ? now : now.minusDays(1) ;
            return attendanceRepository.findByAlreadyCheckedSchedule(id,endDay.toLocalDate());
        }
        if(type.equals("workChangeId")){
            return attendanceRepository.findByAlreadyCheckedWorkChangeId(id);
        }
        return null;
    }
    //퇴근
    public void leaveCheck(Attendance attendance){
        attendance.setActualEnd(LocalDateTime.now());
        attendanceRepository.save(attendance);
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

            if (schedule.getDay() == dayOfWeek) { // 금일 스케줄 여부 확인
                totalScheduled++; // 금일 스케줄에 해당하면 카운트 증가
                if (attendance == null) {
                    // Attendance 데이터가 없으면
                    notYetStarted++; // 출근 전 상태
                } else if (attendance.getActualStart() == null) {
                    // 출근 기록이 없으면
                    notYetStarted++; // 출근 전 상태
                } else {
                    // 출근 기록이 있으면
                    attended++; // 출근 상태
                }
            } else {
                // 금일 스케줄이 아니면 휴무로 간주
                onLeave++;
            }
        }

        // 출근율 계산
        double attendanceRate = totalScheduled > 0 ? ((double) attended / totalScheduled) * 100 : 0;

        // DTO 생성 및 반환
        return new AttendanceDetailsDTO(totalScheduled, attended, onLeave, notYetStarted, attendanceRate);
    }
}
