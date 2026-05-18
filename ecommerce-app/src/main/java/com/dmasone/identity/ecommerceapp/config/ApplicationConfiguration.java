package com.dmasone.identity.ecommerceapp.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cross-module application beans that should be owned by the bootstrap module,
 * not by any individual business module.
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
