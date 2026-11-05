package org.example.notification.service.dispatcher;

import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.service.notification.DeliveryDispatcher;
import org.example.notification.service.notification.delivery.NotificationDeliveryLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(DeliveryDispatcherTestBase.NotificationChannelTestConfig.class)
public abstract class DeliveryDispatcherTestBase {

    DeliveryDispatcher dispatcher;

    @Autowired
    @Qualifier("emailNotificationChannel")
    NotificationChannel emailChannel;

    @Autowired
    @Qualifier("smsNotificationChannel")
    NotificationChannel smsChannel;

    @MockBean
    NotificationDeliveryLogService deliveryLogService;

    @Autowired
    @Qualifier("notificationExecutor")
    private Executor notificationExecutor;

    @BeforeEach
    void setUp() {
        reset(emailChannel, smsChannel);
        when(emailChannel.type()).thenReturn(ChannelType.EMAIL);
        when(smsChannel.type()).thenReturn(ChannelType.SMS);
        List<NotificationChannel> channelBeans =
                List.of(emailChannel, smsChannel);
        dispatcher = new DeliveryDispatcher(
               channelBeans,
                deliveryLogService,
                notificationExecutor,
                3,
                10
        );
    }

    protected Notification notification(ChannelType... channelTypes) {
        return Notification.builder()
                .notificationId("test-id")
                .channels(Set.of(channelTypes))
                .build();
    }

    protected void mockEmailRecipients(String... recipients) {
        when(emailChannel.resolveReceipts(any()))
                .thenReturn(List.of(recipients));
    }

    protected void mockSmsRecipients(String... recipients) {
        when(smsChannel.resolveReceipts(any()))
                .thenReturn(List.of(recipients));
    }

    protected void mockSuccessfulEmailDelivery() {
        mockEmailRecipients("test@example.com");

        when(emailChannel.send(any(), anyString()))
                .thenReturn(DeliveryResult.success());
    }

    protected void mockSuccessfulSmsDelivery() {
        mockSmsRecipients("+48123456789");

        when(smsChannel.send(any(), anyString()))
                .thenReturn(DeliveryResult.success());
    }

    protected void awaitSend(NotificationChannel channel, int count) {
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        verify(channel, times(count))
                                .send(any(), anyString())
                );
    }

    @TestConfiguration
    public static class NotificationChannelTestConfig {

        @Bean(name = "emailNotificationChannel")
        NotificationChannel emailNotificationChannel() {
            NotificationChannel channel = mock(NotificationChannel.class);
            when(channel.type()).thenReturn(ChannelType.EMAIL);
            return channel;
        }

        @Bean(name = "smsNotificationChannel")
        NotificationChannel smsNotificationChannel() {
            NotificationChannel channel = mock(NotificationChannel.class);
            when(channel.type()).thenReturn(ChannelType.SMS);
            return channel;
        }
        
    }
}
