package org.example.notification.channel;

import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;

import java.util.List;

public interface NotificationChannel {
    ChannelType type();
    List<String> resolveReceipts(Policy policy);
    DeliveryResult send(Notification notification, String receipt);
}
