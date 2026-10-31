package org.example.notification.service.notification.event;

import lombok.AllArgsConstructor;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.notification.event.NotificationEvent;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.event.NotificationEventRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationEventService {
    private final NotificationEventRepository notificationEventRepository;

    public NotificationEvent save(Notification notification) throws DatabaseException {
        return notificationEventRepository.save(notification);
    }
}
