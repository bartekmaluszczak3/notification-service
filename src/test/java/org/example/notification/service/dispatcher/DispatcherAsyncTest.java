package org.example.notification.service.dispatcher;

import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class DispatcherAsyncTest extends DeliveryDispatcherTestBase{

    @Test
    void shouldExecuteSendingAsynchronously() throws InterruptedException {
        // given
        Notification notification = notification(ChannelType.EMAIL);

        CountDownLatch sendStarted = new CountDownLatch(1);
        CountDownLatch releaseSend = new CountDownLatch(1);
        mockEmailRecipients("test@example.com");

        when(emailChannel.send(any(), anyString()))
                .thenAnswer(invocation -> {
                    sendStarted.countDown();
                    if (!releaseSend.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException(
                                "Test timeout while waiting for release"
                        );
                    }
                    return DeliveryResult.success();
                });
        CompletableFuture<Void> dispatchFuture =
                CompletableFuture.runAsync(() -> {
                    try {
                        dispatcher.dispatch(notification);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                });

        // when and then
        try {
            assertThat(sendStarted.await(2, TimeUnit.SECONDS))
                    .as("Sending should start")
                    .isTrue();

            assertThat(dispatchFuture)
                    .succeedsWithin(Duration.ofSeconds(2));

            verify(emailChannel, times(1))
                    .send(notification, "test@example.com");

        } finally {
            releaseSend.countDown();
        }

    }

    @Test
    void shouldExecuteMultipleRecipients() throws InterruptedException {
        // given
        Notification notification = notification(ChannelType.EMAIL);

        CountDownLatch bothStarted = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        mockEmailRecipients(
                "first@example.com",
                "second@example.com"
        );

        when(emailChannel.send(any(), anyString()))
                .thenAnswer(invocation -> {
                    bothStarted.countDown();

                    if (!release.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timeout");
                    }

                    return DeliveryResult.success();
                });

        // when and then
        try {
            dispatcher.dispatch(notification);
            assertThat(bothStarted.await(3, TimeUnit.SECONDS))
                    .as("Both deliveries should start concurrently")
                    .isTrue();

        } finally {
            release.countDown();
        }
    }

    @Test
    void shouldRetryFailedDeliveryAndEventuallySucceed() {
        // given
        Notification notification = notification(ChannelType.EMAIL);

        mockEmailRecipients("test@example.com");

        when(emailChannel.send(any(), anyString()))
                .thenReturn(
                        DeliveryResult.failed("first failed"),
                        DeliveryResult.failed("second failed"),
                        DeliveryResult.success()
                );
        // when
        dispatcher.dispatch(notification);

        // then
        awaitSend(emailChannel, 3);
    }

    @Test
    void shouldNotRetryWhenDeliverySucceedsOnFirstAttempt() throws InterruptedException {
        // given
        Notification notification = notification(ChannelType.EMAIL);
        mockSuccessfulEmailDelivery();

        // when
        dispatcher.dispatch(notification);
        awaitSend(emailChannel, 1);
        Thread.sleep(100);

        // then
        verify(emailChannel, times(1))
                .send(any(), anyString());
    }

    @Test
    void shouldRetryEachRecipientIndependently() throws Exception {
        // given
        Notification notification = notification(ChannelType.EMAIL);

        mockEmailRecipients(
                "first@example.com",
                "second@example.com"
        );

        when(emailChannel.send(any(), eq("first@example.com")))
                .thenReturn(DeliveryResult.failed(""), DeliveryResult.success());

        when(emailChannel.send(any(), eq("second@example.com")))
                .thenReturn(DeliveryResult.success());

        // when
        dispatcher.dispatch(notification);

        // then
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(emailChannel, times(2))
                            .send(any(), eq("first@example.com"));

                    verify(emailChannel, times(1))
                            .send(any(), eq("second@example.com"));
                });
    }
}
