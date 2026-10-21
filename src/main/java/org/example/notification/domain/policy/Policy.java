package org.example.notification.domain.policy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.notification.domain.policy.ChannelType;

import java.time.OffsetDateTime;
import java.util.Set;
@NoArgsConstructor
@Getter
@Setter
public class Policy {
    private Long id;
    private String policyId;
    private String meterId;
    private Set<ChannelType> channelTypes;
    private Set<String> emailRecipients;
    private Set<String> smsRecipients;
    private String anomalyType;
    private String severity;
    private boolean enabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
}
