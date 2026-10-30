package org.example.notification.repository.notification.delivery.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.DeliveryStatus;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.delivery.NotificationDeliveryLogRepository;
import org.example.notification.repository.notification.delivery.value.NotificationDeliveryLogQueries;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
@AllArgsConstructor
@Slf4j
public class NotificationDeliveryLogRepositoryPostgres implements NotificationDeliveryLogRepository {
    private final JdbcTemplate jdbc;

    @Override
    public String insertPending(String notificationId, ChannelType channelType, String recipient) throws DatabaseException {
        String deliveryId = UUID.randomUUID().toString();
        Timestamp now = Timestamp.from(Instant.now());
        log.info("Trying to insert notification delivery into the database");
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(NotificationDeliveryLogQueries.INSERT_SQL);

                ps.setObject(1, deliveryId);
                ps.setObject(2, notificationId);
                ps.setString(3, channelType.name());
                ps.setString(4, recipient);
                ps.setString(5, DeliveryStatus.PENDING.name());
                ps.setTimestamp(6, now);
                ps.setTimestamp(7, now);

                return ps;
            });

            return deliveryId;

        } catch (Exception e) {
            log.error("Failed to insert notification delivery: {}", deliveryId, e);
            throw new DatabaseException("Failed to insert notification delivery");
        } finally {
            log.info("Finished inserting notification delivery:: {}", deliveryId);
        }
    }

    @Override
    public void updateResult(String deliveryId, DeliveryResult result, int attempts) throws DatabaseException {
        log.info("Trying to update notification delivery in the database");

        try {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime sentAt = result.deliveryStatus() == DeliveryStatus.SUCCESS
                            ? OffsetDateTime.now()
                            : null;

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(NotificationDeliveryLogQueries.UPDATE_SQL);

                ps.setString(1, result.deliveryStatus().name());
                ps.setObject(2, result.message(), Types.VARCHAR);
                ps.setObject(3, sentAt, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setInt(4, Math.max(0, attempts - 1));
                ps.setObject(5, now, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setObject(6, deliveryId);

                return ps;
            });

        } catch (Exception e) {
            log.error("Failed to update delivery result: {}", deliveryId, e);
            throw new DatabaseException("Failed to notification delivery  result");
        } finally {
            log.info("Finished updating notification delivery : {}", deliveryId);
        }
    }
}
