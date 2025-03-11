package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final MessageService messageService;

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public ChannelDTO createPublicChannel(CreateChannel.PublicRequest request) {
    Channel channel = Channel.builder()
            .type(ChannelType.PUBLIC)
            .name(request.getName())
            .description(request.getDescription())
            .build();

    channelRepository.save(channel);

    return modelMapper.map(channel, ChannelDTO.class);
  }

  @Transactional
  @Override
  public ChannelDTO createPrivateChannel(CreateChannel.PrivateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    List<ReadStatus> readStatuses = request.getParticipantIds().stream()
            .map(userId -> {
              User user = userRepository.findById(userId)
                      .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
              return new ReadStatus(user, createdChannel, createdChannel.getCreatedAt());
            })
            .toList();

    readStatusRepository.saveAll(readStatuses);

    return modelMapper.map(createdChannel, ChannelDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDTO find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));

    return modelMapper.map(channel, ChannelDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDTO> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

    List<Channel> list = channelRepository.findAll().stream()
            .filter(channel ->
                    channel.getType().equals(ChannelType.PUBLIC)
                            || mySubscribedChannelIds.contains(channel.getId())
            )
            .toList();

    return list.stream().map(channel -> modelMapper.map(channel, ChannelDTO.class)).toList();
  }

  @Transactional
  @Override
  public ChannelDTO update(UUID channelId, UpdatePublicChannel request) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                    () -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new ServiceException(ErrorCode.CANNOT_MODIFY_PRIVATE_CHANNEL);
    }
    channel.update(request.name(), request.description());

    return modelMapper.map(channel, ChannelDTO.class);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                    () -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));

    channelRepository.delete(channel);
  }
}
