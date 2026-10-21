package org.example.notification.service.policy;

import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.exception.PolicyNotFoundException;
import org.example.notification.repository.policy.PolicyRepository;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class GetPolicyServiceTest extends IntegrationBase {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyRepository policyRepository;

    @Test
    void shouldGetPolicyById() throws PolicyNotFoundException, DatabaseException {
        // given
        policyRepository.save(samplePolicy(),
                "policy-id");

        // when
        var policy = policyService.getPolicy("policy-id");

        // then
        Assertions.assertEquals("meter-id", policy.getMeterId());
        Assertions.assertEquals("OVERVOLTAGE", policy.getAnomalyType());
        Assertions.assertEquals("CRITICAL", policy.getSeverity());
        Assertions.assertEquals(Set.of(ChannelType.DATABASE, ChannelType.SMS), policy.getChannelTypes());
        Assertions.assertTrue(policy.isEnabled());
    }

    @Test
    void shouldThrowException() {
        // when and then
        Assertions.assertThrows(PolicyNotFoundException.class, ()-> policyService.getPolicy("invalid-id"));
    }
}