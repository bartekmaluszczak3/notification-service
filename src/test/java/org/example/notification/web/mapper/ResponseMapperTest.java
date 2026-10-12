package org.example.notification.web.mapper;

import org.example.notification.domain.ChannelType;
import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class ResponseMapperTest {
    private ResponseMapper responseMapper = new ResponseMapper();

    @Test
    void shouldMapPolicyToPolicyResponse() {
        // given
        Policy policy = new Policy();
        policy.setPolicyId("policy-id");
        policy.setMeterId("meter-id");
        policy.setChannelTypes(Set.of(ChannelType.DATABASE, ChannelType.SMS, ChannelType.EMAIL));
        policy.setEmailRecipients(Set.of("testmail@example.com"));
        policy.setSmsRecipients(Set.of("123456789"));
        policy.setAnomalyType("OVERLOAD");
        policy.setSeverity("CRITICAL");
        policy.setEnabled(false);

        // when
        PolicyResponse policyResponse = responseMapper.toResponse(policy);

        // then
        Assertions.assertEquals("policy-id", policyResponse.getPolicyId());
        Assertions.assertEquals("meter-id", policyResponse.getMeterId());
        Assertions.assertEquals(Set.of(ChannelType.DATABASE, ChannelType.SMS, ChannelType.EMAIL), policyResponse.getChannelTypes());
        Assertions.assertEquals(Set.of("testmail@example.com"), policyResponse.getEmailRecipients());
        Assertions.assertEquals(Set.of("123456789"), policyResponse.getSmsRecipients());
        Assertions.assertEquals("OVERLOAD", policyResponse.getAnomalyType());
        Assertions.assertFalse(policyResponse.isEnabled());
    }
}
