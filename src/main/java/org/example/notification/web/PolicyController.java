package org.example.notification.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.domain.dto.PolicyRequest;
import org.example.notification.domain.dto.PolicyResponse;
import org.example.notification.service.PolicyService;
import org.example.notification.web.mapper.ResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/policies")
@AllArgsConstructor
@Slf4j
public class PolicyController {
    private final ResponseMapper responseMapper;
    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@RequestBody PolicyRequest policyRequest) {
        log.info("Received create policy request");
        log.debug("Anomaly type = {} channels {} meterId {} severity {}", policyRequest.getAnomalyType(),
                policyRequest.getChannelTypes(), policyRequest.getMeterId(), policyRequest.getSeverity());
        var policy = policyService.save(policyRequest);
        return ResponseEntity.ok(responseMapper.toResponse(policy));
    }
}
