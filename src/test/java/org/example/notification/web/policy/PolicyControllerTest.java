package org.example.notification.web.policy;

import lombok.SneakyThrows;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.domain.policy.dto.PolicyResponse;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.service.policy.PolicyService;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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

    @Test
    void shouldUpdatePolicy() throws DatabaseException {
        // given
        Policy policy = policyService.save(samplePolicy());
        UpdatePolicyRequest updatePolicyRequest = UpdatePolicyRequest.builder()
                .channelTypes(Set.of(ChannelType.SMS, ChannelType.DATABASE))
                .emailRecipients(Set.of("testmail@example.com"))
                .smsRecipients(Set.of("123456789"))
                .enabled(false)
                .build();


        // when
        ResponseEntity<PolicyResponse> response = testRestTemplate.exchange(
                "/api/v1/notifications/policies/{policyId}",
                HttpMethod.PUT,
                new HttpEntity<>(updatePolicyRequest),
                PolicyResponse.class,
                policy.getPolicyId()
        );
        // then
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void shouldFindPolicy() {
        // given
        Policy policy = policyService.save(samplePolicy());
        String url = "/api/v1/notifications/policies?meterId=meter-id&anomalyType=OVERVOLTAGE&severity=CRITICAL";

        // when
        var response = testRestTemplate.getForEntity(url,  PolicyResponse.class);

        // then
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        var body = response.getBody();
        Assertions.assertEquals(Set.of(ChannelType.DATABASE, ChannelType.SMS), body.getChannelTypes());
    }


}
