package com.example.FinalProject;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.repository.contract.ContractRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
class FinalProjectApplicationTests {

	@Autowired
	private ContractRepository contractRepository;

	@Test
	public void testFindValidContractByUserIdAndDateRange() {
		String userId = "user11";
		LocalDate startDate = LocalDate.of(2023, 12, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);

//		Optional<Contract> contract = contractRepository.findValidContractByUserIdAndDateRange(userId, startDate, endDate);
//		Assertions.assertTrue(contract.isPresent(), "유효한 계약 정보가 없습니다!");
	}

}
