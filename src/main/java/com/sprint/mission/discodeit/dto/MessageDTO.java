package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String content;
    private String writerId;
    private String channelName;

    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .content(message.getContent())
                .writerId(message.getWriter().getPhone())
                .channelName(message.getChannel().getName())
                .build();
    }
}
