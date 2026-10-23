CREATE TABLE policies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    policy_id VARCHAR(50)  NOT NULL UNIQUE,
    meter_id VARCHAR(50) NOT NULL,
    anomaly_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    channels VARCHAR[] NOT NULL,
    email_recipients VARCHAR[],
    sms_recipients VARCHAR[],
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT policies_meter_anomaly_severity_unique
        UNIQUE (meter_id, anomaly_type, severity)
);

CREATE TABLE IF NOT EXISTS notification_events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    notification_id VARCHAR(50) NOT NULL UNIQUE,
    event_id VARCHAR(50) NOT NULL,
    meter_id VARCHAR(100) NOT NULL,
    anomaly_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    detected_value NUMERIC(10, 2),
    threshold NUMERIC(10, 2),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMPTZ
);