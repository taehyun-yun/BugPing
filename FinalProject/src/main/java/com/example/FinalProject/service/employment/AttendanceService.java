package com.example.FinalProject.service.employment;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.DailyAttendanceDTO;
import com.example.FinalProject.dto.OvertimeRequestDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final ScheduleRepository scheduleRepository;
    private final WorkChangeRepository workChangeRepository;
    private final WorkRepository workRepository;

    @Autowired
    public AttendanceService (AttendanceRepository attendanceRepository,CompanyRepository companyRepository, ScheduleRepository scheduleRepository, WorkChangeRepository workChangeRepository,  WorkRepository workRepository){
        this.attendanceRepository = attendanceRepository;
        this.companyRepository = companyRepository;
        this.scheduleRepository = scheduleRepository;
        this.workChangeRepository = workChangeRepository;
        this.workRepository = workRepository;
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

    // 추가 근무 업데이트 로직
    public void updateOvertime(OvertimeRequestDTO overtimeRequestDTO) {
        if (overtimeRequestDTO.getOvertimeEnd() == null) {
            throw new IllegalArgumentException("추가 근무 종료 시간이 null입니다.");
        }

        // overtimeStart와 overtimeEnd를 받아서 계산
        LocalDateTime overtimeStart = Optional.ofNullable(overtimeRequestDTO.getOvertimeStart())
                .orElseThrow(() -> new IllegalArgumentException("추가 근무 시작 시간이 null입니다."));

        LocalDateTime overtimeEnd = overtimeRequestDTO.getOvertimeEnd();

        // 추가 근무 시간이 동일한지 체크
        if (overtimeStart.isEqual(overtimeEnd)) {
            throw new IllegalArgumentException("추가 근무 시작 시간과 종료 시간이 동일합니다. 시간을 다시 확인해주세요.");
        }

        // 시간 차이를 계산
        Duration duration = Duration.between(overtimeStart, overtimeEnd);

        // 추가 근무 시간이 4시간 이상일 경우 휴게 시간을 30분씩 추가
        long overtimeMinutes = duration.toMinutes();
        int overtimeBreakMinute = 0;

        // 4시간 이상 근무할 경우 휴게 시간 계산
        if (overtimeMinutes >= 240) {
            overtimeBreakMinute = (int) (overtimeMinutes / 60) * 30; // 1시간마다 30분 휴게시간
        }

        // 실제 추가 근무 시간 계산 (총 시간에서 휴게 시간 제외)
        int overtimeMinute = (int) overtimeMinutes - overtimeBreakMinute;

        // DB에서 해당 출근 기록을 찾음
        Attendance attendance = attendanceRepository.findById(overtimeRequestDTO.getAttendanceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 출근 기록을 찾을 수 없습니다."));

        // 추가 근무 데이터 설정
        attendance.setOvertimeStart(overtimeStart);
        attendance.setOvertimeEnd(overtimeEnd);
        attendance.setOvertimeStatus("추가 근무");
        attendance.setOvertimeBreakMinute(overtimeBreakMinute);
        attendance.setOvertimeMinute(overtimeMinute);

        // DB에 저장
        attendanceRepository.save(attendance);

        // 로그 출력
        log.info("추가 근무 시간 저장 완료: {}, 휴게 시간: {}, 실 근무 시간: {}", overtimeMinutes, overtimeBreakMinute, overtimeMinute);
    }
}
