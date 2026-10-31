package org.example.notification.service.policy;

import lombok.SneakyThrows;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.policy.PolicyRepository;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.Set;

public class UpdatePolicyServiceTest extends IntegrationBase {

    @Autowired
    private PolicyService policyService;
    @Autowired
    private PolicyRepository policyRepository;

    @SneakyThrows
    @Test
    void shouldUpdatePolicy() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        policyRepository.save(samplePolicy(),
                "policy-id");
        UpdatePolicyRequest updatePolicyRequest = UpdatePolicyRequest.builder()
                .channelTypes(Set.of(ChannelType.SMS))
                .emailRecipients(Set.of("testmail@example.com"))
                .smsRecipients(Set.of("123456789"))
                .enabled(false)
                .build();

        // when
        var policy = policyService.update("policy-id", updatePolicyRequest);

        // then
        Assertions.assertEquals(Set.of("123456789"), policy.getSmsRecipients());
        Assertions.assertEquals(Set.of("testmail@example.com"), policy.getEmailRecipients());
        Assertions.assertEquals(Set.of(ChannelType.SMS), policy.getChannelTypes());
        Assertions.assertFalse(policy.isEnabled());
        Assertions.assertTrue(now.isBefore(policy.getModifiedAt()));
    }

    @SneakyThrows
    @Test
    void shouldNotUpdateWhenPolicyNotExist() {
        // given
        UpdatePolicyRequest updatePolicyRequest = UpdatePolicyRequest.builder()
                .channelTypes(Set.of(ChannelType.SMS))
                .emailRecipients(Set.of("testmail@example.com"))
                .smsRecipients(Set.of("123456789"))
                .enabled(false)
                .build();

        // when + then
        Assertions.assertThrows(DatabaseException.class, ()-> policyService.update("invalid-id", updatePolicyRequest));
    }
}
