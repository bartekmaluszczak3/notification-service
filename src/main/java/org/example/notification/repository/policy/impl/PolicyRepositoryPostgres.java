package org.example.notification.repository.policy.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.policy.value.PolicyQueries;
import org.example.notification.repository.policy.PolicyRepository;
import org.example.notification.repository.policy.mapper.PolicyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class PolicyRepositoryPostgres implements PolicyRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PolicyRowMapper policyRowMapper;

    PolicyRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.policyRowMapper = new PolicyRowMapper();
    }

    @Override
    public Policy save(PolicyRequest request, String policyId) throws DatabaseException {
        log.info("Trying to save policy in the database");
        try {
            return jdbcTemplate.query(
                            connection -> {
                                PreparedStatement ps = connection.prepareStatement(PolicyQueries.INSERT_SQL);

                                String[] emails = request.getEmailRecipients() != null
                                        ? request.getEmailRecipients().toArray(new String[0])
                                        : new String[0];

                                String[] channels = request.getChannelTypes().stream()
                                        .map(ChannelType::name)
                                        .toArray(String[]::new);

                                String[] sms = request.getSmsRecipients() != null
                                        ? request.getSmsRecipients().toArray(new String[0])
                                        : new String[0];
                                ps.setObject(1, policyId);
                                ps.setObject(2, request.getMeterId());
                                ps.setObject(3, request.getAnomalyType());
                                ps.setObject(4, request.getSeverity());
                                ps.setArray(5, connection.createArrayOf("VARCHAR", channels));
                                ps.setArray(6, connection.createArrayOf("VARCHAR", emails));
                                ps.setArray(7, connection.createArrayOf("VARCHAR", sms));
                                ps.setBoolean(8, request.isEnabled());

                                return ps;
                            },
                            policyRowMapper
                    ).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "INSERT did not return the saved policy"
                    ));
        } catch (Exception e) {
            log.error("Failed to save policy: {}", policyId, e);
            throw new DatabaseException("Failed to save policy");
        } finally {
            log.info("Policy saved");
        }

    }

    @Override
    public Optional<Policy> getByPolicyId(String policyId) {
        log.info("Searching for policy with id = {}", policyId);
        List<Policy> policies = jdbcTemplate.query(
                PolicyQueries.FIND_BY_ID,
                policyRowMapper,
                policyId
        );

        return policies.stream().findFirst();
    }

    @Override
    public Policy update(String policyId, UpdatePolicyRequest request) throws DatabaseException {
        log.info("Trying to update policy in the databse");
        try {
            return jdbcTemplate.query(
                            connection -> {
                                PreparedStatement ps = connection.prepareStatement(PolicyQueries.UPDATE_SQL);

                                String[] emails = request.getEmailRecipients() != null
                                        ? request.getEmailRecipients().toArray(new String[0])
                                        : new String[0];

                                String[] channels = request.getChannelTypes().stream()
                                        .map(ChannelType::name)
                                        .toArray(String[]::new);

                                String[] sms = request.getSmsRecipients() != null
                                        ? request.getSmsRecipients().toArray(new String[0])
                                        : new String[0];

                                ps.setObject(1, channels);
                                ps.setObject(2, emails);
                                ps.setObject(3, sms);
                                ps.setBoolean(4, request.isEnabled());
                                ps.setObject(5, policyId);

                                return ps;
                            },
                            policyRowMapper
                    ).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "UPDATE did not return the saved policy"
                    ));
        } catch (Exception e) {
            log.error("Failed to update policy: {}", policyId, e);
            throw new DatabaseException("Failed to save policy");
        } finally {
            log.info("Policy updated");
        }
    }

    @Override
    public Policy findPolicy(String meterId, String anomalyType, String severity) {
        log.info("Trying to find policy in the database");
        return jdbcTemplate.query(
                        connection -> {
                            PreparedStatement ps = connection.prepareStatement(PolicyQueries.FIND_SQL);
                            ps.setObject(1, meterId);
                            ps.setObject(2,anomalyType );
                            ps.setObject(3, severity);
                            return ps;
                        },
                        policyRowMapper
                ).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Find did not return  policy"
                ));
    }
}
