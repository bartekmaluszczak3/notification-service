package org.example.notification.repository.notification.event.value;

public class NotificationEventQueries {
    public static final String INSERT_SQL = """
            INSERT INTO notification_events (
            notification_id, event_id, meter_id, anomaly_type, severity, detected_value, threshold, description, processed_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
}
