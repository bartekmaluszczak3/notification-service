package org.example.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.notification.domain.ChannelType;

import java.util.Set;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePolicyRequest {
        private Set<ChannelType> channelTypes;
        private Set<String> emailRecipients;
        private Set<String> smsRecipients;
        private boolean enabled;

    }
