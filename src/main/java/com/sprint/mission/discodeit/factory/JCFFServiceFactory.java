package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.HashMap;

public class JCFFServiceFactory implements ServiceFactory{
    private static JCFFServiceFactory instance;
    private final JCFUserService userService;
    private final JCFMessageService messageService;
    private final JCFChannelService channelService;

    private JCFFServiceFactory() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService();
        this.messageService = new JCFMessageService(userService, channelService);
    }

    public static JCFFServiceFactory getInstance() {
        if (instance == null) {
            instance = new JCFFServiceFactory();
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
