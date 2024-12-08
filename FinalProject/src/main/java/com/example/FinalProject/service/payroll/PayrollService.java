package com.example.FinalProject.service.payroll;

import com.example.FinalProject.dto.AttendanceDTO;
import com.example.FinalProject.dto.ContractDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Slf4j
@Service
public class PayrollService {

    // 급여 계산 메서드
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO requestDTO) {
        // Mock 데이터를 가져옴
        List<AttendanceDTO> attendanceList = getMockAttendanceData(requestDTO.getEmployeeId(), requestDTO.getStartDate(), requestDTO.getEndDate());
        ContractDTO contractDTO = getMockContractData(requestDTO.getEmployeeId());

        double totalBasicSalary = 0;
        double totalOverTimePay = 0;

        // 근무시간 기반 급여 계산
        for (AttendanceDTO attendanceDTO : attendanceList) {
            int regularHours = attendanceDTO.getRecognizedworkHour().getHour(); // 정규 근무 시간
            int overtimeHours = attendanceDTO.getOvertimeHours().getHour(); // 초과 근무 시간

            totalBasicSalary += regularHours * contractDTO.getHourlyWage();
            totalOverTimePay += overtimeHours * contractDTO.getHourlyWage() * 1.5; // 초과 근무 1.5배

        }

        double totalSalary = totalBasicSalary = totalOverTimePay; // 총 급여

        // 로그 출력
        log.info("급여 계산 결과: totalBasicSalary={}, totalOverTimePay={}, totalSalary={}",
                totalBasicSalary, totalOverTimePay, totalSalary);
        // 응답 DTO 생성
        return new PayrollResponseDTO(
                requestDTO.getEmployeeId(),
                "Taehyun", // 임시 이름
                totalBasicSalary,
                totalOverTimePay,
                totalSalary
        );
    } // end calculatePayroll

    // Mock 데이터 가져오기 (Attendance와 Contract)
    private List<AttendanceDTO> getMockAttendanceData(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        // Mock 데이터 생성
        AttendanceDTO attendance1 = new AttendanceDTO(1, 1, LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(8, 0), LocalTime.of(2, 0), "정상");
        AttendanceDTO attendance2 = new AttendanceDTO(2, 1, LocalTime.of(10, 0), LocalTime.of(19, 0), LocalTime.of(8, 0), LocalTime.of(1, 0), "정상");
        return List.of(attendance1, attendance2);
    }

    private ContractDTO getMockContractData(Integer employeeId) {
        // Mock Contract 데이터 생성
        return new ContractDTO(1, "Example Company", 10000, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    }
}
