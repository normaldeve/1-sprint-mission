package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService  {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        channelRepository.findByName(request.name()).ifPresent(channel -> {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);});
        Channel channel = new Channel(request.name(), request.description(), request.channelFormat(), ChannelType.PUBLIC);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public PrivateChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        List<User> users = request.users();
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user.getId(), request.channelId());
            readStatusRepository.save(readStatus);
        }

        Channel channel = new Channel(null, null, request.channelFormat(), ChannelType.PRIVATE);
        channelRepository.save(channel);
        return new PrivateChannelDto(channel.getId(), channel.getChannelType(), channel.getChannelFormat());
    }
    @Override
    public Optional<Channel> getChannelByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> getAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateType(Channel channel, ChannelType channelType) {
        existChannel(channel);
        channel.changeType(channelType);
        channelRepository.save(channel);
        return channel;
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
