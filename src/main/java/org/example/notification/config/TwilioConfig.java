package org.example.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.twilio")
public record TwilioConfig(
        String accountSid,
        String authToken,
        String fromNumber
) {
}
