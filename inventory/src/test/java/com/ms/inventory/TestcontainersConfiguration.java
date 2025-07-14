package com.ms.inventory;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {


	// 1) un solo contenedor, estático, arrancado en el bloque
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:latest"))
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	static {
		postgres.start();
	}

	// 2) exponemos ese mismo objeto como bean @ServiceConnection
	@Bean
	@ServiceConnection
	public PostgreSQLContainer<?> postgresContainer() {
		return postgres;
	}

	// 3) mapeamos sus URL/credenciales a propiedades de Spring
	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url",      postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

}
