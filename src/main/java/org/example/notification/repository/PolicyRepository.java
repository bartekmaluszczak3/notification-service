package org.example.notification.repository;

import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.exception.DatabaseException;

import java.util.Optional;

public interface PolicyRepository {
    Policy save(PolicyRequest request, String policyId) throws DatabaseException;
    Optional<Policy> getByPolicyId(String policyId);
}
