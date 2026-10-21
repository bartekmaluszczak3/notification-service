package org.example.notification.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.notification.domain.policy.ChannelType;

import java.util.Collections;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {
    private String meterId;
    @Builder.Default
    private Set<ChannelType> channelTypes = Set.of(ChannelType.DATABASE);
    @Builder.Default
    private Set<String> emailRecipients = Collections.emptySet();
    @Builder.Default
    private Set<String> smsRecipients = Collections.emptySet();
    private String anomalyType;
    private String severity;
    @Builder.Default
    private boolean enabled = true;
}
