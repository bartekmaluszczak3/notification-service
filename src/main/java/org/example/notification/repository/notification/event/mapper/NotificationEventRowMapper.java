package org.example.notification.repository.notification.event.mapper;

import org.example.notification.domain.notification.event.NotificationEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class NotificationEventRowMapper implements RowMapper<NotificationEvent> {

    @Override
    public NotificationEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setNotificationId(rs.getString("notification_id"));
        notificationEvent.setEventId(rs.getString("event_id"));
        notificationEvent.setMeterId(rs.getString("meter_id"));
        notificationEvent.setAnomalyType(rs.getString("anomaly_type"));
        notificationEvent.setSeverity(rs.getString("severity"));
        notificationEvent.setDescription(rs.getString("description"));
        notificationEvent.setDetectedValue(rs.getBigDecimal("detected_value"));
        notificationEvent.setThreshold(rs.getBigDecimal("threshold"));
        notificationEvent.setDetectedAt(rs.getObject("created_at", OffsetDateTime.class));
        notificationEvent.setProcessedAt(rs.getObject("processed_at", OffsetDateTime.class));
        return notificationEvent;
    }
}
