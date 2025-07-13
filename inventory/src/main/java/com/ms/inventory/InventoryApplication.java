package com.ms.inventory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER;

@SpringBootApplication
@OpenAPIDefinition(security = @SecurityRequirement(name = "X-API-KEY"))
@SecurityScheme(name = "X-API-KEY", type = SecuritySchemeType.APIKEY, in = HEADER, paramName = "X-API-KEY")
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

}
