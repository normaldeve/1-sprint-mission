package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.PublicChannel;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody CreateChannel.PublicRequest request) {
    Channel publicChannel = channelService.createPublicChannel(request);
    return ResponseEntity.ok(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody CreateChannel.PrivateRequest request) {
    Channel privateChannel = channelService.createPrivateChannel(request);
    return ResponseEntity.ok(privateChannel);
  }

  @GetMapping
  public ResponseEntity<List<Channel>> getAllChannels(@RequestParam("userId") UUID userId) {
    List<Channel> allChannel = channelService.findAllPrivate(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(allChannel);
  }

  @GetMapping("/public")
  public ResponseEntity<List<Channel>> getAllPublicChannels() {
    List<Channel> allPublic = channelService.findAllPublic();
    return ResponseEntity.ok(allPublic);
  }

  // 채널 정보 수정은 Public channel 만 가능합니다.
  @PatchMapping("/{channelId}")
  public ResponseEntity<PublicChannel> updateChannel(
      @PathVariable("channelId") UUID channelId,
      @RequestBody UpdatePublicChannel request) {
    PublicChannel update = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(update);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> deletePrivateChannel(@PathVariable("channelId") UUID channelId) {
    Channel removeChannel = channelService.delete(channelId);
    return ResponseEntity.ok(
        "Remove Private Channel ID: " + removeChannel.getId() +
            " delete complete!"
    );
  }
}
