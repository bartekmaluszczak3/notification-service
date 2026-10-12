package org.example.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.notification.domain.ChannelType;

import java.util.Collections;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {
    private String policyId;
    private String meterId;
    private Set<ChannelType> channelTypes;
    private Set<String> emailRecipients;
    private Set<String> smsRecipients;
    private String anomalyType;
    private String severity;
    private boolean enabled;

}
