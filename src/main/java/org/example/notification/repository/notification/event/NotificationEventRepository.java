package org.example.notification.repository.notification.event;


import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.notification.event.NotificationEvent;
import org.example.notification.exception.DatabaseException;

public interface NotificationEventRepository {
    NotificationEvent save(Notification notification) throws DatabaseException;
}
