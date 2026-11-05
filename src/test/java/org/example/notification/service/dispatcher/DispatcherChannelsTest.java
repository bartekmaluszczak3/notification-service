package org.example.notification.service.dispatcher;

import lombok.SneakyThrows;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class DispatcherChannelsTest extends DeliveryDispatcherTestBase {

    @Test
    void shouldDispatchToEmailChannel() {
        // given
        Notification notification = notification(ChannelType.EMAIL);
        mockSuccessfulEmailDelivery();

        // when
        dispatcher.dispatch(notification);

        // then
        awaitSend(emailChannel, 1);
        verify(smsChannel, never()).resolveReceipts(any());
        verify(smsChannel, never()).send(any(), anyString());
    }

    @Test
    void shouldDispatchToSmsChannel() {
        // given
        Notification notification = notification(ChannelType.SMS);
        mockSuccessfulSmsDelivery();

        // when
        dispatcher.dispatch(notification);

        // then
        awaitSend(smsChannel, 1);
        verify(emailChannel, never()).resolveReceipts(any());
        verify(emailChannel, never()).send(any(), anyString());
    }

    @Test
    void shouldDispatchToSmsAndEmail() {
        // given
        Notification notification = notification(ChannelType.SMS, ChannelType.EMAIL);
        mockSuccessfulSmsDelivery();
        mockSuccessfulEmailDelivery();

        // when
        dispatcher.dispatch(notification);

        // then
        awaitSend(emailChannel, 1);
        awaitSend(emailChannel, 1);
    }

    @Test
    void shouldNotDispatchToChannels() {
        // given
        Notification notification = notification();
        mockSuccessfulSmsDelivery();
        mockSuccessfulEmailDelivery();

        //when
        dispatcher.dispatch(notification);

        // then
        verify(emailChannel, never()).resolveReceipts(any());
        verify(emailChannel, never()).send(any(), anyString());
    }

    @Test
    void shouldDispatchToAllEmailReceipts() {
        // given
        Notification notification = notification(ChannelType.EMAIL);

        mockEmailRecipients(
                "first@example.com",
                "second@example.com",
                "third@example.com"
        );
        when(emailChannel.send(any(), anyString()))
                .thenReturn(DeliveryResult.success());

        // when
        dispatcher.dispatch(notification);

        // then

        awaitSend(emailChannel, 3);

        verify(emailChannel).send(notification, "first@example.com");
        verify(emailChannel).send(notification, "second@example.com");
        verify(emailChannel).send(notification, "third@example.com");
    }

    @Test
    void shouldDispatchToAllSMSlReceipts() {
        // given
        Notification notification = notification(ChannelType.SMS);

        mockSmsRecipients(
                "123456789",
                "987654321"
        );
        when(smsChannel.send(any(), anyString()))
                .thenReturn(DeliveryResult.success());

        // when
        dispatcher.dispatch(notification);

        // then

        awaitSend(smsChannel, 2);
        verify(smsChannel).send(notification, "123456789");
        verify(smsChannel).send(notification, "987654321");
    }

    @Test
    void shouldNotSendAnythingWhenListOfReceiptsIsEmpty() {
        // given
        Notification notification = notification(ChannelType.SMS, ChannelType.EMAIL);
        when(emailChannel.resolveReceipts(any()))
                .thenReturn(List.of());
        when(smsChannel.resolveReceipts(any()))
                .thenReturn(List.of());

        // when
        dispatcher.dispatch(notification);

        verify(emailChannel, never()).send(any(), anyString());
        verify(smsChannel, never()).send(any(), anyString());
    }
}
