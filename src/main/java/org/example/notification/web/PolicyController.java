package org.example.notification.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.domain.dto.PolicyResponse;
import org.example.notification.exception.DatabaseException;
import org.example.notification.exception.PolicyNotFoundException;
import org.example.notification.service.PolicyService;
import org.example.notification.web.mapper.ResponseMapper;
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
}
