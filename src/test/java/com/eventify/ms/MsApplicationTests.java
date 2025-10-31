package com.eventify.ms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.eventify.ms.config.IntegrationTestConfig;

@Import(IntegrationTestConfig.class)
@SpringBootTest
class MsApplicationTests {

	@Test
	void contextLoads() {
	}

}
