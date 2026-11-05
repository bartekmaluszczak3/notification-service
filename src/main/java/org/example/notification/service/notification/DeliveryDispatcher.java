package org.example.notification.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.DeliveryStatus;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.service.notification.delivery.NotificationDeliveryLogService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeliveryDispatcher {
    private final Map<ChannelType, NotificationChannel> channels;
    private final NotificationDeliveryLogService deliveryLogService;
    private final Executor executor;
    private final int maxAttempts;
    private final Duration baseBackoff;

    public DeliveryDispatcher(List<NotificationChannel> channelBeans, NotificationDeliveryLogService notificationDeliveryLogService,
                              @Qualifier("notificationExecutor") Executor executor, @Value("${service.notification.retry-max-attempts:3}") int maxAttempts,
                                      @Value("${notification.retry.base-backoff-ms:1000}") long baseBackoffMs) {
        this.channels = channelBeans.stream().collect(Collectors.toMap(NotificationChannel::type, c-> c));
        this.deliveryLogService = notificationDeliveryLogService;
        this.executor = executor;
        this.maxAttempts = maxAttempts;
        this.baseBackoff = Duration.ofMillis(baseBackoffMs);
    }

    public void dispatch(Notification notification){
        for(ChannelType channelType: notification.getChannels()) {
            NotificationChannel notificationChannel = channels.get(channelType);
            if (notificationChannel == null) {
                log.warn("No channel implementation found for {}", channelType);
            }else {
                for(String recipient: notificationChannel.resolveReceipts(notification)) {
                    Optional<String> deliveryId = deliveryLogService.insertPending(notification.getNotificationId(), channelType, recipient);
                    executor.execute(() -> sendWithRetry(deliveryId, notification, notificationChannel, recipient));
                }
            }
        }
    }
    private void sendWithRetry(Optional<String> deliveryId, Notification notification, NotificationChannel notificationChannel, String recipient) {
        DeliveryResult result = DeliveryResult.failed("not attempted");
        int attempt = 0;
        while (attempt < maxAttempts) {
            attempt++;
            result = notificationChannel.send(notification, recipient);
            if (result.deliveryStatus() != DeliveryStatus.FAILED) {
                break;
            }
            if (attempt < maxAttempts) {
                sleep(baseBackoff.multipliedBy(1L << (attempt - 1)));
            }
        }
        if (deliveryId.isPresent()) {
            deliveryLogService.updateResult(deliveryId.get(), result, attempt);
        }else {
            log.error("Cannot create delivery log");
        }
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
