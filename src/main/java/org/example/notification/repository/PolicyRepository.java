package org.example.notification.repository;

import org.example.notification.domain.Policy;
import org.example.notification.domain.dto.PolicyRequest;

public interface PolicyRepository {
    Policy save(PolicyRequest request, String policyId);
}
