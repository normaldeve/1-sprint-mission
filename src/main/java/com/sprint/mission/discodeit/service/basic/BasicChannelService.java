package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
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
    public ChannelDTO.PublicChannel createPublicChannel(CreateChannel.PublicRequest request) {
        channelRepository.findByName(request.getName()).ifPresent(channel -> {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);});
        Channel channel = new Channel(request.getName(), request.getDescription(), request.getChannelFormat(), ChannelType.PUBLIC);
        channelRepository.save(channel);
        return ChannelDTO.PublicChannel.fromDomain(channel);
    }

    @Override
    public ChannelDTO.PrivateChannel createPrivateChannel(CreateChannel.PrivateRequest request) {
        List<User> users = request.getJoinUser();
        Channel channel = new Channel(null, null, request.getChannelFormat(), ChannelType.PRIVATE);
        channelRepository.save(channel);
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
            readStatusRepository.save(readStatus);
        }

        return ChannelDTO.PrivateChannel.fromDomain(channel, users);
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
