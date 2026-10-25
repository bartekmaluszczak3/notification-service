package org.example.notification.channel;

import jakarta.mail.internet.MimeMessage;
import org.example.notification.Application;
import org.example.notification.channel.impl.EmailNotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.DeliveryStatus;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.Policy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmailNotificationChannelTest {

    @Autowired
    private EmailNotificationChannel emailNotificationChannel;

    @Autowired
    private TemplateEngine templateEngine;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void shouldRenderNotification() {
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

        // when
        Context context = new Context();
        context.setVariable("notification", notification);

        String html = templateEngine.process(
                "notification-email",
                context
        );
        Assertions.assertTrue(html.contains("meter-id"));
        Assertions.assertTrue(html.contains("event-id"));
        Assertions.assertTrue(html.contains("anomaly-type"));
        Assertions.assertTrue(html.contains("severity"));
        Assertions.assertTrue(html.contains("description"));
        Assertions.assertTrue(html.contains("253.75"));
    }

    @Test
    void shouldResolveReceipts() {
        // given
        Policy policy = new Policy();
        policy.setEmailRecipients(Set.of("test@test.com", "example@example.com"));

        // when
        var receipts = emailNotificationChannel.resolveReceipts(policy);

        // then
        Assertions.assertEquals(List.of("test@test.com", "example@example.com"), receipts);
    }

    @Test
    void shouldSendEmail() {
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

        MimeMessage mimeMessage =
                new JavaMailSenderImpl().createMimeMessage();

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        DeliveryResult deliveryResult = emailNotificationChannel.send(notification, "example@example.com");

        // then
        Assertions.assertEquals(DeliveryStatus.SUCCESS, deliveryResult.deliveryStatus());
    }

    @Test
    void shouldReturnErrorWhenCannotSendMail() {
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

        MimeMessage mimeMessage =
                new JavaMailSenderImpl().createMimeMessage();

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Error")).when(javaMailSender).send(any(MimeMessage.class));

        // when
        DeliveryResult deliveryResult = emailNotificationChannel.send(notification, "example@example.com");

        // then
        Assertions.assertEquals(DeliveryStatus.FAILED, deliveryResult.deliveryStatus());
    }
}
