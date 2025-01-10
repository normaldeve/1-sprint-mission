package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, User writer, Channel channel);

    List<Message> getMessageByUser(User writer);

    List<Message> getMessageByChannel(String channelName);

    Message updateMessageContent(UUID id, String newContent);

    List<Message> getAllMessages();

    boolean deleteMessageByWriter(User writer, UUID messageId); // 작성자가 특정 메시지를 삭제하기

    void deleteAllMessageByWriter(User writer); // 작성자 회원이 삭제되었을 때 해당 메시지도 모두 조회되면 안된다

    void deleteAllMessageByChannel(Channel channel); // 채널이 사라졌을 때 해당 채널에 속한 메시지도 모두 조회되면 안 됨
}
