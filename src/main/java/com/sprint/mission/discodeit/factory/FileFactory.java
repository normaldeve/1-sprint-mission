package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class FileFactory implements Factory{
    private static FileFactory instance;
    private UserService userService;
    private MessageService messageService;
    private ChannelService channelService;

    private FileFactory() {
        this.userService = new FileUserService("users.dat");
        this.messageService = new FileMessageService("messages.dat");
        this.channelService = new FileChannelService("channels.dat");
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
