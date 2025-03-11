package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public UserStatusDTO create(UUID userId) {
        validUser(userId);

        userStatusRepository.findByUserId(userId)
                .ifPresent(userStatus -> {
                    throw new ServiceException(ErrorCode.ALREADY_EXIST_USERSTAUTS);
                });

        UserStatusDTO userStatusDTO = UserStatusDTO.builder()
                .userId(userId)
                .lastActiveAt(Instant.now())
                .build();

        UserStatus userStatus = modelMapper.map(userStatusDTO, UserStatus.class);
        userStatusRepository.save(userStatus);
        return userStatusDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDTO find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));

        return modelMapper.map(userStatus, UserStatusDTO.class);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDTO findByUserId(UUID userId) {
        validUser(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));

        return modelMapper.map(userStatus, UserStatusDTO.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDTO> findAll() {
        List<UserStatus> userStatusList = userStatusRepository.findAll();

        // UserStatus 리스트를 UserStatusDTO 리스트로 변환
        return userStatusList.stream()
                .map(userStatus -> modelMapper.map(userStatus, UserStatusDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public UserStatusDTO updateByUserId(UUID userId) {
        validUser(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
        userStatus.update();
        userStatusRepository.save(userStatus);
        return modelMapper.map(userStatus, UserStatusDTO.class);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private void validUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }
}
