package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository {
    User create(String name, String phone, String password);
}
