package com.dmasone.identity.ecommerceapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the OpenAPI model from metadata loaded through {@code openapi.yaml}.
 * Endpoint matching, grouping, and Swagger UI paths are configured directly
 * with springdoc properties in that same YAML file.
 */
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfiguration {

    @Bean
    public OpenAPI ecommerceOpenApi(OpenApiProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.title())
                        .version(properties.version())
                        .description(properties.description())
                        .contact(new Contact()
                                .name(properties.contact().name())
                                .url(properties.contact().url())));
    }
}
