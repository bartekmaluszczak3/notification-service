package org.example.notification.repository;

import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;

import java.util.Optional;

public interface PolicyRepository {
    Policy save(PolicyRequest request, String policyId);
    Optional<Policy> getByPolicyId(String policyId);
}
