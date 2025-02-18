package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/create/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@RequestBody CreateChannel.PublicRequest request) {
        Channel publicChannel = channelService.createPublicChannel(request);
        return ResponseEntity.ok(publicChannel);
    }

    @RequestMapping(value = "/create/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody CreateChannel.PrivateRequest request) {
        Channel privateChannel = channelService.createPrivateChannel(request);
        return ResponseEntity.ok(privateChannel);
    }

    @RequestMapping(value = "/get/public", method = RequestMethod.GET)
    public ResponseEntity<ChannelDTO.PublicChannelDTO> getPublicChannel(@RequestParam("id") UUID uuid) {
        ChannelDTO.PublicChannelDTO publicChannel = channelService.findPublicChannel(uuid);
        return ResponseEntity.ok(publicChannel);
    }

    @RequestMapping(value = "/get/private", method = RequestMethod.GET)
    public ResponseEntity<ChannelDTO.PrivateChannelDTO> getPrivateChannel(@RequestParam("id") UUID uuid) {
        ChannelDTO.PrivateChannelDTO privateChannel = channelService.findPrivateChannel(uuid);
        return ResponseEntity.ok(privateChannel);
    }

    @RequestMapping(value = "getAll/private", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> getAllPrivateChannel(@RequestParam("id") UUID userId) {
        List<Channel> allPrivate = channelService.findAllPrivate(userId);
        return ResponseEntity.ok(allPrivate);
    }

    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> getAllPrivateChannel() {
        List<Channel> allChannel = channelService.findAll();
        return ResponseEntity.ok(allChannel);
    }

    @RequestMapping(value = "delete/private", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePrivateChannel(@RequestParam("id") UUID uuid) {
        Channel removeChannel = channelService.deletePrivate(uuid);
        return ResponseEntity.ok(
                "Remove Private Channel ID: " + removeChannel.getId() +
                        " delete complete!"
        );
    }

    @RequestMapping(value = "delete/public", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePublicChannel(@RequestParam("id") UUID uuid) {
        Channel removeChannel = channelService.deletePublic(uuid);
        return ResponseEntity.ok(
                "Remove Public Channel ID: " + removeChannel.getId() +
                        " delete complete!"
        );
    }
}
