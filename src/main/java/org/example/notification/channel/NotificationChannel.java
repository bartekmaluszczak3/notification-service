package org.example.notification.channel;

import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;

import java.util.List;

public interface NotificationChannel {
    ChannelType type();
    List<String> resolveReceipts(Notification notification);
    DeliveryResult send(Notification notification, String receipt);
}
