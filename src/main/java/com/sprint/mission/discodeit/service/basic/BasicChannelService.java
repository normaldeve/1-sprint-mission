package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.*;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final MessageService messageService;

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel createPublicChannel(CreateChannel.PublicRequest request) {
    Channel channel = new PublicChannel(request.getName(), request.getDescription());
    channelRepository.save(channel);
    return channel;
  }

  @Override
  public Channel createPrivateChannel(CreateChannel.PrivateRequest request) {
    List<UUID> users = request.getJoinUser();

    // User가 UserRepository에 저장되어 있는지 확인
    for (UUID userID : users) {
      Optional<User> userOptional = userRepository.findById(userID);
      if (userOptional.isEmpty()) {
        throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
      }
    }
    // Private 채널 생성 후 저장
    Channel channel = new PrivateChannel(users);
    channelRepository.save(channel);

    // User마다 ReadStatus 생성하기
    for (UUID userID : users) {
      Instant lastReadAt = Instant.now();
      ReadStatus readStatus = new ReadStatus(userID, channel.getId(), lastReadAt);
      readStatusRepository.save(readStatus);
    }

    return channel;
  }
  
  @Override
  public List<Channel> findAllPrivate(UUID userId) {
    validUser(userId);
    List<Channel> allChannels = channelRepository.findAll();

    // PrivateChannel 타입 필터링 및 joinMembers에 userId가 있는지 확인
    return allChannels.stream()
        .filter(channel -> channel instanceof PrivateChannel)
        .map(channel -> (PrivateChannel) channel)
        .filter(privateChannel -> privateChannel.getJoinMembers().contains(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Channel> findAllPublic() {
    List<Channel> allChannels = channelRepository.findAll();

    // PrivateChannel 타입 필터링 및 joinMembers에 userId가 있는지 확인
    return allChannels.stream()
        .filter(channel -> channel instanceof PublicChannel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Channel> findAll() {
    return channelRepository.findAll();
  }

  @Override
  public PublicChannel update(UUID channelId,
      UpdatePublicChannel request) { // 채널에 새로운 유저 참여, 채널 이름, 설명 수정 가능
    validChannel(channelId);
    validUser(request.newUserID());

    PublicChannel updateChannel = (PublicChannel) channelRepository.findById(channelId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    updateChannel.update(request.name(), request.description(), request.newUserID());

    channelRepository.save(updateChannel);
    return updateChannel;
  }

  @Override
  public Channel delete(UUID channelId) {
    validChannel(channelId);

    Channel removeChannel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));

    // 채널에 포함된 메시지 삭제하기
    List<Message> messages = messageService.findAllByChannelId(channelId);
    if (!messages.isEmpty()) {
      messages.forEach(message -> messageRepository.delete(message));
    }

    if (removeChannel instanceof PrivateChannel) {
      // 채널에 포함된 ReadStatus 삭제하기
      List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
      readStatusRepository.deleteAll(readStatuses);
    }

    channelRepository.delete(removeChannel);
    return removeChannel;
  }

  // 채널 검증
  private void validChannel(UUID channelId) {
    channelRepository.findById(channelId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
  }

  // 유저 검증
  private void validUser(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
  }
}
