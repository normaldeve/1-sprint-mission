package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.HashMap;

public class JCFFactory implements Factory{
    private static JCFFactory instance;
    private final UserService userService;
    private final MessageService messageService;
    private final ChannelService channelService;

    private JCFFactory() {
        this.userService = new JCFUserService(new HashMap<>());
        this.messageService = new JCFMessageService(new HashMap<>());
        this.channelService = new JCFChannelService(new HashMap<>());
    }

    public static JCFFactory getInstance() {
        if (instance == null) {
            instance = new JCFFactory();
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
