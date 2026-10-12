package org.example.notification.web.mapper;

import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {
    public PolicyResponse toResponse(Policy policy) {
        return PolicyResponse.builder()
                .anomalyType(policy.getAnomalyType())
                .channelTypes(policy.getChannelTypes())
                .policyId(policy.getPolicyId())
                .enabled(policy.isEnabled())
                .meterId(policy.getMeterId())
                .emailRecipients(policy.getEmailRecipients())
                .severity(policy.getSeverity())
                .smsRecipients(policy.getSmsRecipients())
                .build();
    }
}
