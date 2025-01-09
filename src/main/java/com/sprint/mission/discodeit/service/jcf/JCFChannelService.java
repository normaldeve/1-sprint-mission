package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelRepository;

    public JCFChannelService() {
        this.channelRepository = new HashMap<>();
    }

    @Override
    public void createChannel(Channel channel) {
        if (channelRepository.get(channel.getId()) == null) {
            channelRepository.put(channel.getId(), channel);
            System.out.println("채널 생성이 완료되었습니다.");
        } else {
            System.out.println("이미 존재하는 채널입니다.");
        }
    }

    @Override
    public Channel getChannelById(UUID id) {
        if (channelRepository.get(id) != null) {
            return channelRepository.get(id);
        } else {
            System.out.println("유효하지 않는 채널 ID이거나 존재하지 않는 채널입니다");
            return null;
        }
    }

    @Override
    public Channel updateChannelById(UUID id, User newUser) { //새로운 유저가 채널에 들어갈때
        if (channelRepository.get(id) != null) {
            Channel channel = getChannelById(id);
            channel.update(newUser);
            return channel;
        } else {
            System.out.println("유효하지 않는 채널 ID이거나 존재하지 않는 채널입니다.");
            return null;
        }
    }

    @Override
    public void deleteChannel(Channel channel) {
        if (channelRepository.get(channel.getId()) != null) {
            channelRepository.remove(channel.getId());
            System.out.println("채널 삭제가 완료되었습니다.");
        } else {
            System.out.println("채널이 존재하지 않거나 이미 삭제된 채널입니다");
        }
    }
}
