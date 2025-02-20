package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class UserStatusController {
    private final UserStatusService userStatusService;

    @RequestMapping(value = "/get/userid", method = RequestMethod.GET)
    public ResponseEntity<UserStatus> getStatusByUserId(@RequestParam("id") UUID userId) {
        UserStatus userStatus = userStatusService.findByUserId(userId);
        return ResponseEntity.ok(userStatus);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<UserStatus> getStatusById(@RequestParam("id") UUID id) {
        UserStatus userStatus = userStatusService.find(id);
        return ResponseEntity.ok(userStatus);
    }
}
