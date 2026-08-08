package com.gabrieltiziano.event_driven_basket.order_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class OrderServiceApplicationTests {
	@MockBean
	JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
