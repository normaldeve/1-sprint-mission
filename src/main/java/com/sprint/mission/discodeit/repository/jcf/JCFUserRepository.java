package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap;

    public JCFUserRepository() {
        this.userMap = new HashMap<>();
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userMap.values().stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public User delete(User user) {
        userMap.remove(user.getId());
        return user;
    }
}
