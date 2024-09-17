package ru.nsu.ostest.adapter.in.rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type= SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "OS Test application")
        },
        security = {@SecurityRequirement(name = "bearerAuth")},
        info = @Info(
                title = "OS Test API",
                description = "OS Test application",
                version = "v1"
        )
)
public class SpringdocConfiguration {
}
