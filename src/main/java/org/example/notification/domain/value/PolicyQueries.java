package org.example.notification.domain.value;

public final class PolicyQueries {
    public static final String INSERT_SQL = """
            INSERT INTO policies (
            policy_id, meter_id, anomaly_type, severity, channels, email_recipients, sms_recipients, enabled)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;

}
