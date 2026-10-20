package org.example.notification.repository.value;

public final class PolicyQueries {
    public static final String INSERT_SQL = """
            INSERT INTO policies (
            policy_id, meter_id, anomaly_type, severity, channels, email_recipients, sms_recipients, enabled)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    public static final String FIND_BY_ID = """
            SELECT * 
            FROM policies 
            WHERE policy_id = ?
            """;
}
