package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.*;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.FindChannel;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService  {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublicChannel(CreateChannel.PublicRequest request) {
        channelRepository.findByName(request.getName()).ifPresent(channel -> {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);});
        Channel channel = new PublicChannel(request.getName(), request.getDescription(), request.getChannelFormat());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivateChannel(CreateChannel.PrivateRequest request) {
        List<User> users = request.getJoinUser();
        Channel channel = new PrivateChannel(users, request.getChannelFormat());
        channelRepository.save(channel);
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    @Override
    public FindChannel.PublicResponse findPublicChannel(UUID channelId) {
        PublicChannel findChannel = (PublicChannel) channelRepository.findById(channelId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));

        // 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        Instant latestMessageTime = Instant.EPOCH; // 초기값 설정
        List<Message> messageList = messageRepository.findAll();

        for (Message message : messageList) {
            if (message.getChannel().equals(findChannel)) {
                if (message.getCreatedAt().isAfter(latestMessageTime)) {
                    latestMessageTime = message.getCreatedAt();
                }
            }
        }
        return FindChannel.PublicResponse.fromDomain(findChannel, latestMessageTime);
    }

    @Override
    public FindChannel.PrivateResponse findPrivateChannel(UUID channelId) {
        PrivateChannel findChannel = (PrivateChannel) channelRepository.findById(channelId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
        if (findChannel.getChannelType().equals(ChannelType.PUBLIC)) {
            throw new ServiceException(ErrorCode.CHANNEL_TYPE_MISMATCH);
        }

        // 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
        Instant latestMessageTime = Instant.EPOCH; // 초기값 설정
        List<Message> messageList = messageRepository.findAll();

        for (Message message : messageList) {
            if (message.getChannel().equals(findChannel)) {
                if (message.getCreatedAt().isAfter(latestMessageTime)) {
                    latestMessageTime = message.getCreatedAt();
                }
            }
        }

        //Private 채널인 경우 참여한 User의 Id 정보를 포함합니다.
        return FindChannel.PrivateResponse.fromDomain(findChannel, latestMessageTime);
    }

    @Override
    public List<Channel> findAllPublic() {

    }

    @Override
    public List<Channel> findAllPrivate() {
        return List.of();
    }


    @Override
    public Channel updateDescription(Channel channel, String description) {
        existChannel(channel);
        channel.changeDescription(description);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void delete(Channel removeChannel) {
        existChannel(removeChannel);
        channelRepository.delete(removeChannel);
    }

    private void existChannel(Channel channel) {
        channelRepository.findByName(channel.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }
}
