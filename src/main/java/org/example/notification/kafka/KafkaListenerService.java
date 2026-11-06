package org.example.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.device.events.AnomalyDetectedEvent;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.notification.event.NotificationEvent;
import org.example.notification.domain.policy.Policy;
import org.example.notification.exception.DatabaseException;
import org.example.notification.service.notification.DeliveryDispatcher;
import org.example.notification.service.notification.event.NotificationEventService;
import org.example.notification.service.policy.PolicyService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaListenerService {
    private final PolicyService policyService;
    private final NotificationEventService eventService;
    private final DeliveryDispatcher dispatcher;


    @KafkaListener(topics = "anomaly.detected", groupId = "notification-service")
    @Transactional
    public void handle(AnomalyDetectedEvent event) {
        log.info("Received anomaly detected event");
        Policy policy = policyService.findPolicy(event.getMeterId(), event.getAnomalyType().name(), event.getSeverity().name());
        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .eventId(event.getEventId())
                .meterId(event.getMeterId())
                .anomalyType(event.getAnomalyType().name())
                .severity(event.getSeverity().name())
                .description(event.getDescription())
                .detectedValue(BigDecimal.valueOf(event.getDetectedValue()))
                .threshold(BigDecimal.valueOf(event.getThreshold()))
                .processedAt(event.getOccurredAt())
                .channels(policy.getChannelTypes())
                .smsRecipients(policy.getSmsRecipients())
                .emailRecipients(policy.getEmailRecipients())
                .build();
        try {
            eventService.save(notification);
        } catch (DatabaseException e) {
            log.error("Cannot save notification in database");
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatcher.dispatch(notification);
                }
            });
        } else {
            dispatcher.dispatch(notification);
        }
    }
}
