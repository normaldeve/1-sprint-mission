package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class FileFactory implements Factory{
    private static FileFactory instance;
    private final UserService userService;
    private final MessageService messageService;
    private final ChannelService channelService;

    private FileFactory() {
        this.userService = new FileUserService("./result/users.ser");
        this.messageService = new FileMessageService("./result/messages.ser");
        this.channelService = new FileChannelService("./result/channels.ser");
    }

    public static FileFactory getInstance() {
        if (instance == null) {
            instance = new FileFactory();
        }
        return instance;
    }
    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public MessageService getMessageService() {
        return messageService;
    }

    @Override
    public ChannelService getChannelService() {
        return channelService;
    }
}