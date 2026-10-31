package org.example.notification.service.policy;

import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.policy.PolicyRepository;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


public class FindPolicyServiceTest extends IntegrationBase {
    @Autowired
    PolicyService policyService;

    @Autowired
    PolicyRepository policyRepository;
    @Test
    void shouldFindPolicy() throws DatabaseException {
        // given
        policyRepository.save(samplePolicy(),
                "policy-id");

        // when
        var policy = policyService.findPolicy("meter-id", "OVERVOLTAGE", "CRITICAL");

        // then
        Assertions.assertEquals(Set.of(ChannelType.SMS), policy.getChannelTypes());
    }

    @Test
    void shouldReturnDefaultPolicyWhenPolicyIsNotFound() {
        // when
        var policy = policyService.findPolicy("meter-id", "OVERVOLTAGE", "CRITICAL");

        // then
        Assertions.assertEquals("meter-id", policy.getMeterId());
    }
}
