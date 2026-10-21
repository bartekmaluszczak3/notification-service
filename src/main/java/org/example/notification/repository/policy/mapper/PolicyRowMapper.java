package org.example.notification.repository.policy.mapper;

import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PolicyRowMapper implements RowMapper<Policy> {

    @Override
    public Policy mapRow(ResultSet rs, int rowNum) throws SQLException {
        Policy policy = new Policy();
        policy.setId(rs.getLong("id"));
        policy.setPolicyId(rs.getString("policy_id"));
        policy.setMeterId(rs.getString("meter_id"));
        policy.setAnomalyType(rs.getString("anomaly_type"));

        policy.setChannelTypes(
                Arrays.stream((Object[]) rs.getArray("channels").getArray())
                        .map(Object::toString)
                        .map(ChannelType::valueOf)
                        .collect(Collectors.toSet())
        );

        policy.setSeverity(rs.getString("severity"));
        policy.setEmailRecipients(toStringSet(rs.getArray("email_recipients")));
        policy.setSmsRecipients(toStringSet(rs.getArray("sms_recipients")));
        policy.setEnabled(rs.getBoolean("enabled"));

        policy.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        policy.setModifiedAt(rs.getObject("modified_at", OffsetDateTime.class));
        return policy;
    }

    private Set<String> toStringSet(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return Collections.emptySet();
        }

        return Arrays.stream((Object[]) sqlArray.getArray())
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
