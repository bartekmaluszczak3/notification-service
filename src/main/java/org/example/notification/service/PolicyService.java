package org.example.notification.service;

import lombok.AllArgsConstructor;
import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.domain.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.exception.PolicyNotFoundException;
import org.example.notification.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
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
}
