package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;


import java.util.*;

import static com.sprint.mission.discodeit.error.ChannelError.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelRepository;
    private static final ChannelService instance = new JCFChannelService();
    private final MessageService messageService;

    private JCFChannelService() {
        this.channelRepository = new HashMap<>();
        this.messageService = JCFMessageService.getInstance();
    }

    public static ChannelService getInstance() {
        return instance;
    }

    @Override
    public Channel createChannel(List<User> members, String name, User creator) throws IllegalArgumentException {
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                throw new IllegalArgumentException(DUPLICATE_NAME.getMessage());
            }
        }
        Channel channel = new Channel(members, name, creator);
        channelRepository.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannelByName(String name) {
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                return channel;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_NAME.getMessage());
    }

    @Override
    public List<Channel> getChannelsByUserId(User user) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : channelRepository.values()) {
            if (channel.getMembers().contains(user)) {
                channels.add(channel);
            }
        }
        if (!channels.isEmpty()) {
            return channels;
        }
        throw new IllegalArgumentException(EMPTY_CHANNEL.getMessage());
    }

    @Override
    public Channel addUserToChannel(String name, User newUser) {//새로운 유저가 채널에 들어갈때
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                channel.update(newUser);
                return channel;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_NAME.getMessage());
    }

    @Override
    public List<Channel> getAllChannel() {
        if (channelRepository.values() == null) {
            throw new IllegalArgumentException(EMPTY_CHANNEL.getMessage());
        }
        return new ArrayList<>(channelRepository.values());
    }

    @Override
    public Channel removeUserFromChannel(String name, User removeUser) {
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                channel.removeUser(removeUser);
                return channel;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_NAME.getMessage());
    }

    @Override
    public boolean deleteChannel(String name) { // 채널 삭제 시 해당 채널에 있는 메시지도 모두 삭제
        for (Channel channel : channelRepository.values()) {
            if (channel.getName() == name) {
                channelRepository.remove(channel.getId());
                System.out.println(channel.getName() + " 채널이 삭제되었습니다");
                for (Message message : messageService.getMessageByChannel(channel.getName())) {
                    messageService.deleteAllMessageByChannel(channel);
                }
                return true;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_NAME.getMessage());
    }
}