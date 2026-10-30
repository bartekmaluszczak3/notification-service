package org.example.notification.repository.notification.delivery;

import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;

public interface NotificationDeliveryLogRepository {
    String insertPending(String notification, ChannelType channelType, String recipient) throws DatabaseException;
    void updateResult(String deliveryId, DeliveryResult result, int attempts) throws DatabaseException;
}
