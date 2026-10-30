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


create TABLE IF NOT EXISTS notification_delivery_log (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    delivery_id VARCHAR(50) NOT NULL UNIQUE,
    notification_id VARCHAR(50)  NOT NULL REFERENCES notification_events(notification_id),
    channel VARCHAR(20)  NOT NULL CHECK (channel IN ('DATABASE', 'EMAIL', 'SMS')),
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(20)  NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'SKIPPED')),
    error_message TEXT,
    retry_count int NOT NULL DEFAULT 0,
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
)