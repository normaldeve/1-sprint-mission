package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class Factory {
    private static final UserService userService = new JCFUserService();
    private static final MessageService messageService = new JCFMessageService();
    private static final ChannelService channelService = new JCFChannelService();

    public static UserService getUserService() {
        return userService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }

    public static ChannelService getChannelService() {
        return channelService;
    }
}
