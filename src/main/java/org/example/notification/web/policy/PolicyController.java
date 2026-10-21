package org.example.notification.web.policy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.domain.policy.dto.PolicyResponse;
import org.example.notification.domain.policy.dto.UpdatePolicyRequest;
import org.example.notification.exception.DatabaseException;
import org.example.notification.exception.PolicyNotFoundException;
import org.example.notification.service.policy.PolicyService;
import org.example.notification.web.policy.mapper.ResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/policies")
@AllArgsConstructor
@Slf4j
public class PolicyController {
    private final ResponseMapper responseMapper;
    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@RequestBody PolicyRequest policyRequest) throws DatabaseException {
        log.info("Received create policy request");
        log.debug("Anomaly type = {} channels {} meterId {} severity {}", policyRequest.getAnomalyType(),
                policyRequest.getChannelTypes(), policyRequest.getMeterId(), policyRequest.getSeverity());
        var policy = policyService.save(policyRequest);
        return ResponseEntity.ok(responseMapper.toResponse(policy));
    }

    @GetMapping("{policyId}")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable String policyId) throws PolicyNotFoundException {
        log.info("Received get policy request");
        var policy = policyService.getPolicy(policyId);
        return ResponseEntity.ok(responseMapper.toResponse(policy));
    }

    @PutMapping("{policyId}")
    public ResponseEntity<PolicyResponse> update(@PathVariable String policyId, @RequestBody UpdatePolicyRequest updatePolicyRequest) throws DatabaseException {
        log.info("Received update policy request");
        var updatedPolicy = policyService.update(policyId, updatePolicyRequest);
        return ResponseEntity.ok(responseMapper.toResponse(updatedPolicy));
    }
    @GetMapping()
    public ResponseEntity<PolicyResponse> findPolicy(@RequestParam String meterId, @RequestParam String anomalyType,
                                                     @RequestParam String severity) {
        log.info("Received find policy request");
        var policy = policyService.findPolicy(meterId, anomalyType, severity);
        return ResponseEntity.ok(responseMapper.toResponse(policy));
    }
}
