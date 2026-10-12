package org.example.notification.service;

import lombok.AllArgsConstructor;
import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    public Policy save(PolicyRequest policyRequest) {
        String policyId = UUID.randomUUID().toString();
        return policyRepository.save(policyRequest, policyId);
    }
}
