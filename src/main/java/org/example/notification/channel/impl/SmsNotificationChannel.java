package org.example.notification.channel.impl;

import lombok.AllArgsConstructor;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.service.policy.sms.SmsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SmsNotificationChannel implements NotificationChannel {
    private final SmsService smsService;

    @Override
    public ChannelType type() {
        return ChannelType.SMS;
    }

    @Override
    public List<String> resolveReceipts(Policy policy) {
        return policy.getSmsRecipients().stream().toList();
    }

    @Override
    public DeliveryResult send(Notification notification, String receipt) {
        String message = """
                ANOMALY DETECTED
                Severity: %s
                Meter: %s
                Type: %s
                Value: %s
                Description: %s
                """.formatted(
                notification.getSeverity(),
                notification.getMeterId(),
                notification.getAnomalyType(),
                notification.getDetectedValue(),
                notification.getDescription()
        );
        smsService.sendSms(message, receipt);
        return DeliveryResult.success();
    }
}
