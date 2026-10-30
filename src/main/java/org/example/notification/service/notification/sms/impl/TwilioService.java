package org.example.notification.service.notification.sms.impl;

import com.twilio.type.PhoneNumber;
import org.example.notification.config.TwilioConfig;
import org.example.notification.service.notification.sms.SmsService;
import org.springframework.stereotype.Component;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;

@Component
public class TwilioService implements SmsService {
    private final TwilioRestClient client;
    private final String fromNumber;

    public TwilioService(TwilioConfig twilioConfig) {
        this.client = new TwilioRestClient.Builder(
                twilioConfig.accountSid(),
                twilioConfig.authToken()
        ).build();
        this.fromNumber = twilioConfig.fromNumber();
    }

    @Override
    public void sendSms(String message, String recipient) {
        Message.creator(
                new PhoneNumber(recipient),
                new PhoneNumber(fromNumber),
                message
        ).create(client);
    }
}
