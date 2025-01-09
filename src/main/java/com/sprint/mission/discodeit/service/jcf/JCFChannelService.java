package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelRepository;
    private UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.channelRepository = new HashMap<>();
    }

    @Override
    public Channel createChannel(List<User> members, String name, User creator) {
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                System.out.println("이미 존재하는 채널입니다");
                return null;
            }
        }
        Channel channel = new Channel(members, name, creator);
        channelRepository.put(channel.getId(), channel);
        System.out.println("채널이 생성되었습니다.");
        return channel;
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
            Channel channel = channelRepository.get(id);
            channel.update(newUser);
            return channel;
        } else {
            System.out.println("유효하지 않는 채널 ID이거나 존재하지 않는 채널입니다.");
            return null;
        }
    }

    @Override
    public List<Channel> getAllChannel() {
        return new ArrayList<>(channelRepository.values());
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
