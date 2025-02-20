package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/readstatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<ReadStatus> getStatus(@RequestParam("id") UUID uuid) {
        ReadStatus readStatus = readStatusService.find(uuid);
        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(value = "/get/userid", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> getStatusesByUserId(@RequestParam("id") UUID userID) {
        List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userID);
        return ResponseEntity.ok(allByUserId);
    }
}
