package com.example.FinalProject;

import com.example.FinalProject.dto.ScheduleDTO;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class FinalProjectApplicationTests {

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Test
	public void testFindByContract_Work_User_UserId() {
		String userId = "testUserId"; // 테스트용 사용자 ID

		List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);

		schedules.forEach(schedule -> {
			System.out.println("Schedule ID: " + schedule.getScheduleId());
			System.out.println("Day: " + schedule.getDay());
			System.out.println("Start Time: " + schedule.getOfficialStart());
			System.out.println("End Time: " + schedule.getOfficialEnd());
		});
	}
}
