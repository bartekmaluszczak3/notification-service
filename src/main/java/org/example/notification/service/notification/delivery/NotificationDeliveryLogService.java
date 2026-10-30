package org.example.notification.service.notification.delivery;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.delivery.NotificationDeliveryLogRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationDeliveryLogService {
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;

    public Optional<String> insertPending(String notification, ChannelType channelType, String recipient){
        try {
            return Optional.ofNullable(notificationDeliveryLogRepository.insertPending(notification, channelType, recipient));
        } catch (DatabaseException e) {
            log.error("Cannot register delivery log. Omitted");
            return Optional.empty();
        }
    }

    public void updateResult(String deliveryId, DeliveryResult result, int attempts) {
        try {
            notificationDeliveryLogRepository.updateResult(deliveryId, result, attempts);
        } catch (DatabaseException e) {
            log.error("Cannot updated delivery log. Omitted");
        }
    }
}
