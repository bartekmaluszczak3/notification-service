package org.example.notification.repository.policy;

import org.example.notification.domain.policy.Policy;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;

import java.util.Optional;

public interface PolicyRepository {
    Policy save(PolicyRequest request, String policyId) throws DatabaseException;
    Optional<Policy> getByPolicyId(String policyId);

    Policy update(String policyId, UpdatePolicyRequest updatePolicyRequest) throws DatabaseException;

    Policy findPolicy(String meterId, String anomalyType, String severity);
}
