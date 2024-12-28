package com.example.FinalProject.controller.attendance;

import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.service.employment.AttendanceService;
import com.google.zxing.WriterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class QRController {

    private final AttendanceService attendanceService;
    private final CompanyRepository companyRepository;;

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
            String url = String.format("http://192.168.5.23:5173/qrCheck?companyId=%d",company.get().getCompanyId());
            byte[] qrCode = attendanceService.makeQRCode(600,600,url);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
        } catch ( Exception e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/commuteList")
    public ResponseEntity<Map<String,Object>> checkAttendance(@RequestParam Integer companyId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Map<String , Object> map = new HashMap<>();
        if(userId.equals("anonymousUser")){
            map.put("msg","로그인을 한 상태에서 다시 시도해주세요.");
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }
        //스케쥴
        List<Schedule> schedules = attendanceService.getOneSchedule(userId, companyId);
        if(schedules.isEmpty()){
            map.put("msg","진행 중인 스케쥴이 없습니다.");
            return new ResponseEntity<>(map,HttpStatus.NO_CONTENT);
        }
        map.put("schedules",schedules);
        //변경
        List<WorkChange> workChanges = attendanceService.getOneWorkChange(userId, companyId);
        if(workChanges.isEmpty()){
            map.put("msg","변경된 근무일이 없습니다.");
        }
        map.put("workChanges",workChanges);

        map.put("msg","선택하세요.");
        return new ResponseEntity<>(map ,HttpStatus.OK);
    }
    @PostMapping("/commuteCheck")
    public ResponseEntity<Map<String,Object>>commuteCheck(@RequestBody Map<String,Object>map){
        String type = (String) map.get("type");
        Integer id = (Integer)map.get(type);
        map.clear();//선언 후 필요없으니 정리
        boolean result = false;
        Attendance attendance = attendanceService.already(id,type);
        //출근전
        if(attendance == null){
            if(type.equals("scheduleId")){
                result = attendanceService.commuteCheckByScheduleId(id);
            }
            if(type.equals("workChange")){
                result = attendanceService.commuteCheckByWorkChangeId(id);
            }
            String msg = result ? "처리되었습니다." :"해당 일정에 대한 정보가 없습니다." ;
            map.put("step",1);
            map.put("msg", msg);
        }
        //퇴근전
        else if(attendance.getActualEnd()==null){
            map.put("attendance",attendance);
            map.put("step",2);
            map.put("msg", "퇴근처리하시겠습니까?");
        }
        //퇴근 후
        else {
            map.put("step",3);
            map.put("msg","이미 퇴실 처리되었습니다.");
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @PostMapping("/leaveCheck")
    public ResponseEntity<String> leaveCheck(@RequestBody Attendance attendance){
        attendanceService.leaveCheck(attendance);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}