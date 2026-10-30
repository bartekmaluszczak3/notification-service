package org.example.notification.shared;

import org.example.notification.Application;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationBase {

    private static final DockerImageName TIMESCALE_IMAGE =
            DockerImageName.parse("timescale/timescaledb:2.17.2-pg15")
                    .asCompatibleSubstituteFor("postgres");

    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(TIMESCALE_IMAGE)
                    .withDatabaseName("test_gridflow")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("init.sql");
    static {
        postgres.start();
    }

    @BeforeEach
    void beforeEach() {
        clear();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
        registry.add(
                "spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver"
        );
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected void clear() {
        jdbcTemplate.update("""
            DELETE FROM policies;
            DELETE FROM notification_delivery_log;
            DELETE FROM notification_events;
        """);
    }

    protected List<Map<String, Object>> getAllPolicies() {
        return jdbcTemplate.queryForList(
                "SELECT * FROM policies"
        );
    }
    protected List<Map<String, Object>> getAllNotificationEvent() {
        return jdbcTemplate.queryForList(
                "SELECT * FROM notification_events"
        );
    }

    protected List<Map<String, Object>> getNotificationByDeliveryId(String deliveryId) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM notification_delivery_log WHERE delivery_id ='" + deliveryId + "';"
        );
    }


    public PolicyRequest samplePolicy() {
        return PolicyRequest.builder()
                .meterId("meter-id")
                .anomalyType("OVERVOLTAGE")
                .severity("CRITICAL")
                .channelTypes(Set.of(ChannelType.DATABASE, ChannelType.SMS))
                .enabled(true)
                .build();
    }

}
