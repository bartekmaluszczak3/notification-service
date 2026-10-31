package org.example.notification.service.policy;

import lombok.SneakyThrows;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.service.policy.PolicyService;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class CreatePolicyServiceTest extends IntegrationBase {

    @Autowired
    private PolicyService policyService;

    @Test
    @SneakyThrows
    void shouldCreatePolicy() {
        // given
        var policyRequest = PolicyRequest.builder()
                .anomalyType("OVERVOLTAGE")
                .meterId("sonar-0001")
                .channelTypes(Set.of(ChannelType.SMS))
                .emailRecipients(Set.of("test@example.com", "example@dummy.com"))
                .smsRecipients(Set.of("123456789", "987654321"))
                .severity("CRITICAL")
                .enabled(true)
                .build();

        // when
        Policy policy = policyService.save(policyRequest);

        // then
        Assertions.assertFalse(policy.getPolicyId().isEmpty());
        Assertions.assertEquals("OVERVOLTAGE", policy.getAnomalyType());
        Assertions.assertEquals("sonar-0001", policy.getMeterId());
        Assertions.assertEquals(Set.of(ChannelType.SMS), policy.getChannelTypes());
        Assertions.assertEquals(Set.of("test@example.com", "example@dummy.com"), policy.getEmailRecipients());
        Assertions.assertEquals(Set.of("123456789", "987654321"), policy.getSmsRecipients());
        Assertions.assertEquals("CRITICAL", policy.getSeverity());
        Assertions.assertTrue(policy.isEnabled());
    }

    @Test
    @SneakyThrows
    void shouldCreatePolicyWithDefaultValues() {
        // given
        var policyRequest = PolicyRequest.builder()
                .anomalyType("OVERVOLTAGE")
                .meterId("sonar-0001")
                .severity("CRITICAL")
                .build();

        // when
        Policy policy = policyService.save(policyRequest);

        // then
        Assertions.assertFalse(policy.getPolicyId().isEmpty());
        Assertions.assertEquals("OVERVOLTAGE", policy.getAnomalyType());
        Assertions.assertEquals("sonar-0001", policy.getMeterId());
        Assertions.assertEquals("CRITICAL", policy.getSeverity());
        Assertions.assertTrue(policy.isEnabled());
    }

    @Test
    @SneakyThrows
    void shouldNotBeAbleToCreatePolicyIfTheSameExist() {
        // given
        var policyRequest = PolicyRequest.builder()
                .anomalyType("OVERVOLTAGE")
                .meterId("sonar-0001")
                .severity("CRITICAL")
                .build();
        policyService.save(policyRequest);

        // when
        Assertions.assertThrows(DatabaseException.class, ()-> policyService.save(policyRequest));


    }
}
