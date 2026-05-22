package com.dmasone.identity.ecommerceapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
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

    private static final String API_ERROR_SCHEMA = "ApiError";
    private static final String APPLICATION_JSON = "application/json";

    @Bean
    public OpenAPI ecommerceOpenApi(OpenApiProperties properties) {
        return new OpenAPI()
                .components(new Components()
                        .addSchemas(API_ERROR_SCHEMA, apiErrorSchema()))
                .info(new Info()
                        .title(properties.title())
                        .version(properties.version())
                        .description(properties.description())
                        .contact(new Contact()
                                .name(properties.contact().name())
                                .url(properties.contact().url())));
    }

    /**
     * Adds the shared error body to documented error responses from module
     * controllers without making business modules depend on the app web DTO.
     */
    @Bean
    public OperationCustomizer apiErrorResponseCustomizer() {
        return (operation, handlerMethod) -> {
            operation.getResponses().forEach((status, response) -> {
                if (status.startsWith("4") || status.startsWith("5")) {
                    attachApiErrorContent(response);
                }
            });
            return operation;
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Schema<?> apiErrorSchema() {
        return new ObjectSchema()
                .addProperty("code", new StringSchema()
                        .description("Stable machine-readable error code"))
                .addProperty("message", new StringSchema()
                        .description("Human-readable error message"))
                .required(List.of("code", "message"));
    }

    @SuppressWarnings("rawtypes")
    private void attachApiErrorContent(ApiResponse response) {
        response.setContent(new Content().addMediaType(
                APPLICATION_JSON,
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + API_ERROR_SCHEMA))
        ));
    }
}
