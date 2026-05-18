package com.dmasone.identity.ecommerceapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAPI metadata loaded from {@code openapi.yaml}. Keeping these values in
 * configuration avoids hard-coded API documentation metadata in application
 * code.
 */
@ConfigurationProperties(prefix = "app.openapi")
public record OpenApiProperties(
        String title,
        String version,
        String description,
        Contact contact
) {

    public record Contact(String name, String url) {
    }
}
