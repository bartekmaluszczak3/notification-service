package org.example.notification.channel;

import org.example.notification.channel.impl.SmsNotificationChannel;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.Policy;
import org.example.notification.service.policy.sms.SmsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
public class SmsNotificationChannelTest {

    @Autowired
    private SmsNotificationChannel smsNotificationChannel;

    @MockBean
    private SmsService smsService;

    @Test
    void shouldSendSms() {
        // given
        Notification notification = Notification.builder()
                .notificationId("notification-id")
                .meterId("meter-id")
                .eventId("event-id")
                .anomalyType("anomaly-type")
                .severity("severity")
                .description("description")
                .detectedValue(new BigDecimal("253.75"))
                .build();

        String recipient = "+48123456789";

        // when
        smsNotificationChannel.send(notification, recipient);

        // then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService).sendSms(messageCaptor.capture(), eq(recipient));
        String message = messageCaptor.getValue();
        Assertions.assertTrue(message.contains("meter-id"));
        Assertions.assertTrue(message.contains("anomaly-type"));
        Assertions.assertTrue(message.contains("severity"));
        Assertions.assertTrue(message.contains("description"));
        Assertions.assertTrue(message.contains("253.75"));
    }

    @Test
    void shouldResolveRecipient() {
        // given
        Policy policy = new Policy();
        policy.setSmsRecipients(Set.of("+1234566", "+1212211221"));

        // when
        List<String> receipts = smsNotificationChannel.resolveReceipts(policy);

        // then
        Assertions.assertEquals(List.of("+1234566", "+1212211221"), receipts);
    }
}
