package org.example.notification.web;

import lombok.SneakyThrows;
import org.example.notification.domain.ChannelType;
import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.domain.dto.PolicyResponse;
import org.example.notification.service.PolicyService;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Set;

public class PolicyControllerTest extends IntegrationBase {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private PolicyService policyService;

    @BeforeEach
    void beforeEach() {
        clear();
    }

    @Test
    void shouldCreatePolicy() {
        // given
        var policyRequest = PolicyRequest.builder()
                .anomalyType("OVERVOLTAGE")
                .meterId("sonar-0001")
                .channelTypes(Set.of(ChannelType.DATABASE, ChannelType.SMS))
                .emailRecipients(Set.of("test@example.com", "example@dummy.com"))
                .smsRecipients(Set.of("123456789", "987654321"))
                .severity("CRITICAL")
                .enabled(true)
                .build();

        // when
        var response = testRestTemplate.postForEntity("/api/v1/notifications/policies", policyRequest, PolicyResponse.class);

        // then
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        var policies = getAllPolicies();
        Assertions.assertEquals(1, policies.size());
    }

    @SneakyThrows
    @Test
    void shouldGetPolicy() {
        // given
        Policy policy = policyService.save(samplePolicy());

        // when
        var response = testRestTemplate.getForEntity("/api/v1/notifications/policies/" + policy.getPolicyId(), PolicyResponse.class);

        // then
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

    }

}
