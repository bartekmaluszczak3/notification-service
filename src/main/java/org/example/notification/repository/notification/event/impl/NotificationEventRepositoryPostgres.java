package org.example.notification.repository.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.notification.event.NotificationEvent;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.event.NotificationEventRepository;
import org.example.notification.repository.notification.event.mapper.NotificationEventRowMapper;
import org.example.notification.repository.notification.event.value.NotificationEventQueries;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Repository
@Slf4j
public class NotificationEventRepositoryPostgres implements NotificationEventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NotificationEventRowMapper notificationEventRowMapper;

    public NotificationEventRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.notificationEventRowMapper = new NotificationEventRowMapper();
    }


    @Override
    public NotificationEvent save(Notification notification) throws DatabaseException {
        log.info("Trying to save notification in the database");
        try {
            return jdbcTemplate.query(
                            connection -> {
                                PreparedStatement ps = connection.prepareStatement(NotificationEventQueries.INSERT_SQL);
                                ps.setObject(1, notification.getNotificationId());
                                ps.setObject(2, notification.getEventId());
                                ps.setObject(3, notification.getMeterId());
                                ps.setObject(4, notification.getAnomalyType());
                                ps.setObject(5, notification.getSeverity());
                                ps.setObject(6, notification.getDetectedValue());
                                ps.setObject(7, notification.getThreshold());
                                ps.setObject(8, notification.getDescription());
                                ps.setObject(9, Timestamp.from(notification.getProcessedAt()));

                                return ps;
                            },
                            notificationEventRowMapper
                    ).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "INSERT did not return the saved policy"
                    ));
        } catch (Exception e) {
            log.error("Failed to save policy: {}", notification.getNotificationId(), e);
            throw new DatabaseException("Failed to save notification");
        } finally {
            log.info("Policy saved");
        }
    }
}
