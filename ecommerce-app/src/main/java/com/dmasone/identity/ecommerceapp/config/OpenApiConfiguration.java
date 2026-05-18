package com.dmasone.identity.ecommerceapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata for generated API documentation. The specification is
 * produced from the running Spring MVC application by springdoc and can be
 * exported during the Maven documentation workflow.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI ecommerceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Modular Monolith E-commerce API")
                        .version("0.1.0")
                        .description("REST API for product reads, order placement, order lookup, and payment lookup.")
                        .contact(new Contact()
                                .name("Portfolio project")
                                .url("https://github.com/<your-github-user>/modular-monolith-ecommerce")));
    }

    @Bean
    public GroupedOpenApi ecommerceApiGroup() {
        return GroupedOpenApi.builder()
                .group("ecommerce")
                .pathsToMatch("/api/**")
                .build();
    }
}
