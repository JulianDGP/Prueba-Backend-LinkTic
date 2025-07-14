package com.ms.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: arranca Spring context y Testcontainers.
 * No pruebas de lógica aquí, sólo valida que no haya errores de wiring.
 */
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
class InventoryApplicationTests {

	@Test
	void contextLoads() {
		/*
		 * Smoke test: arranca Spring context y Testcontainers.
		 * No prueba la lógica aquí, sólo valida que no haya errores de wiring.
		 */
	}

}
