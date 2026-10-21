package org.example.notification.service.policy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.Policy;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.exception.PolicyNotFoundException;
import org.example.notification.repository.policy.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PolicyService {
    private final PolicyRepository policyRepository;

    public Policy save(PolicyRequest policyRequest) throws DatabaseException {
        String policyId = UUID.randomUUID().toString();
        return policyRepository.save(policyRequest, policyId);
    }

    public Policy getPolicy(String policyId) throws PolicyNotFoundException {
        return policyRepository.getByPolicyId(policyId).
                orElseThrow(()->  new PolicyNotFoundException(""));
    }

    public Policy update(String policyId, UpdatePolicyRequest updatePolicyRequest) throws DatabaseException {
        return policyRepository.update(policyId, updatePolicyRequest);
    }

    public Policy findPolicy(String meterId, String anomalyType, String severity) {
        try {
            return policyRepository.findPolicy(meterId, anomalyType, severity);
        }catch (Exception e){
            log.info("Policy not found. Return default policy.");
            var policy = new Policy();
            policy.setEnabled(true);
            policy.setMeterId(meterId);
            policy.setChannelTypes(Set.of(ChannelType.DATABASE));
            return policy;
        }
    }
}
