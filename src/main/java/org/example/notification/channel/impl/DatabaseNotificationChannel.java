package org.example.notification.channel.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.event.NotificationEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class DatabaseNotificationChannel implements NotificationChannel {
    private final NotificationEventRepository notificationEventRepository;
    @Override
    public ChannelType type() {
        return ChannelType.DATABASE;
    }

    @Override
    public List<String> resolveReceipts(Policy policy) {
        return null;
    }

    @Override
    public DeliveryResult send(Notification notification, String receipt) {
        try {
            notificationEventRepository.save(notification);
        } catch (DatabaseException e) {
            log.error("Cannot save notification");
            return DeliveryResult.failed("Cannot save notification");
        }
        return DeliveryResult.success();
    }
}
