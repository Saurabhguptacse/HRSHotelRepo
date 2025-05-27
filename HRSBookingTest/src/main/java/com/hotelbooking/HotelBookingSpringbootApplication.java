package com.hotelbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Main Spring Boot application class for the Hotel Booking MVP.
 * Uses @SpringBootApplication to enable auto-configuration and component scanning.
 */
@SpringBootApplication
public class HotelBookingSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingSpringbootApplication.class, args);
    }

    /**
     * Optional: Bean to log incoming requests, including headers and payload.
     * Useful for debugging and understanding request flow.
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setAfterMessagePrefix("REQUEST DATA: ");
        return loggingFilter;
    }
}
