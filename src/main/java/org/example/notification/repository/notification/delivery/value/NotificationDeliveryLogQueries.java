package org.example.notification.repository.notification.delivery.value;

public class NotificationDeliveryLogQueries {
    public static String INSERT_SQL = """
                         INSERT INTO notification_delivery_log
                                (delivery_id, notification_id, channel, recipient, status, retry_count, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, 0, ?, ?)          
            """;

    public static String UPDATE_SQL = """
                            UPDATE notification_delivery_log
                            SET status = ?, error_message = ?, sent_at = ?, retry_count = ?, updated_at = ?
                            WHERE delivery_id = ?
            """;
}
