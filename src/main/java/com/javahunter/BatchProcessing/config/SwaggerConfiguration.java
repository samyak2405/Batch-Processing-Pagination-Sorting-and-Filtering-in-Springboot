package com.javahunter.BatchProcessing.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Batch Processing Service",
                description = "This is a OpenAPI documentation of a project based that contains demonstration of concepts like " +
                        "Batch Processing, Pagination, Sorting and Filtering and OpenAPI Documentation ",
                version = "1.0",
                license = @License(
                        name = "Apache License v2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                contact = @Contact(
                        name = "Samyak Moon",
                        email = "samyakmoon855@gmail.com",
                        url = "https://www.linkedin.com/in/samyak-moon-1ba527149/"
                )
        ),
        servers = {
                @Server(description = "Local", url = "http://localhost:9090")
        }
//        ,security = @SecurityRequirement(name = "Bearer")
)
//@SecurityScheme(
//        name = "Bearer",
//        type = SecuritySchemeType.HTTP,
//        scheme = "Bearer",
//        bearerFormat = "JWT",
//        in = SecuritySchemeIn.HEADER
//)
@Configuration
public class SwaggerConfiguration {
}
