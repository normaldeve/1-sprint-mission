package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

public interface RepositoryFactory {
    UserRepository getUserRepository();

    MessageRepository getMessageRepository();

    ChannelRepository getChannelRepository();
}
