package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.*;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.FindChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService  {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel createPublicChannel(CreateChannel.PublicRequest request) {
        // User가 UserRepository에 저장되어 있는지 확인
        for (User user : request.getJoinUser()) {
            Optional<User> userOptional = userRepository.findById(user.getId());
            if (userOptional.isEmpty()) {
                throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
            }
        }

        Channel channel = new PublicChannel(request.getName(), request.getDescription(), request.getChannelFormat(), request.getJoinUser());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivateChannel(CreateChannel.PrivateRequest request) {
        List<User> users = request.getJoinUser();

        // User가 UserRepository에 저장되어 있는지 확인
        for (User user : users) {
            Optional<User> userOptional = userRepository.findById(user.getId());
            if (userOptional.isEmpty()) {
                throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
            }
        }
        // Private 채널 생성 후 저장
        Channel channel = new PrivateChannel(users, request.getChannelFormat());
        channelRepository.save(channel);

        // User마다 ReadStatus 생성하기
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
        List<Channel> allChannels = channelRepository.findAll();
        return allChannels.stream()
                .filter(channel -> channel instanceof PublicChannel)  // Public 채널만 필터링
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAllPrivate(UUID userId) { // 통일성을 위해 findAllByUserId보다는 findAllPrivate으로 하였습니다.
        // 해당 userId를 갖는 User가 repository에 저장되어 있는지 확인하기
        validUser(userId);

        // Private 채널 내에서 userId가 같은 회원을 joinMembers 리스트에 갖고 있는 채널만 선택한다.
        List<Channel> allChannels = channelRepository.findAll();
        return allChannels.stream()
                .filter(channel -> channel instanceof PrivateChannel)
                .filter(channel -> channel.getJoinMembers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public Channel update(UpdatePublicChannel.Request request) { // 채널에 새로운 유저 참여, 채널 이름, 설명 수정 가능
        validChannel(request.getChannelId());
        validUser(request.getNewUser().getId());

        PublicChannel updateChannel = (PublicChannel) channelRepository.findById(request.getChannelId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
        updateChannel.update(request.getName(), request.getDescription(), request.getNewUser());

        return updateChannel;
    }

    @Override
    public void delete(UUID channelId) {
        validChannel(channelId);
        Optional<Channel> removeChannel = channelRepository.findById(channelId);
        channelRepository.delete(removeChannel.orElse(null));
    }

    // 채널 검증
    private void validChannel(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }

    // 유저 검증
    private void validUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }
}
