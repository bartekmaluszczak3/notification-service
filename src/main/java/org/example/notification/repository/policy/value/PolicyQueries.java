package org.example.notification.repository.policy.value;

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

    public static final String UPDATE_SQL = """
            UPDATE policies
            SET
                channels = ?,
                email_recipients = ?,
                sms_recipients = ?,
                enabled = ?,
                modified_at = CURRENT_TIMESTAMP
            WHERE policy_id = ?
            RETURNING *
            """;
    public static final String FIND_SQL = """
            SELECT *
            FROM policies
            WHERE meter_id = ?
              AND anomaly_type = ?
              AND severity = ?
            """;
}
