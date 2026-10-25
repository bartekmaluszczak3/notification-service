package org.example.notification.channel.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmailNotificationChannel implements NotificationChannel {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final String TEMPLATE_NAME = "notification-email";

    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

    @Override
    public List<String> resolveReceipts(Policy policy) {
        return policy.getEmailRecipients().stream().toList();
    }

    @Override
    public DeliveryResult send(Notification notification, String receipt) {
        Context context = new Context();
        context.setVariable("notification", notification);

        String htmlContent = templateEngine.process(
                TEMPLATE_NAME,
                context);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    false,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(receipt);
            helper.setSubject("Anomaly detected");
            helper.setText(htmlContent);
            helper.setFrom("noreply@example.com");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Cannot send mail {}", e.getMessage() );
            return DeliveryResult.failed("Cannot send mail " + e.getMessage());
        }
        return DeliveryResult.success();
    }
}
