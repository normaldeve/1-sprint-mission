package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record UpdatePublicChannel(String name, String description, UUID newUserID) {

}
