package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.PublicChannel;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<List<Channel>> getAllChannels() {
    List<Channel> allChannel = channelService.findAll();
    return ResponseEntity.ok(allChannel);
  }

  // 채널 정보 수정은 Public channel 만 가능합니다.
  @PatchMapping("/{channelId}")
  public ResponseEntity<PublicChannel> updateChannel(
      @PathVariable UUID channelId,
      UpdatePublicChannel request) {
    PublicChannel update = channelService.update(channelId, request);
    return ResponseEntity.ok(update);
  }

  @DeleteMapping("/private")
  public ResponseEntity<String> deletePrivateChannel(@RequestParam("id") UUID uuid) {
    Channel removeChannel = channelService.deletePrivate(uuid);
    return ResponseEntity.ok(
        "Remove Private Channel ID: " + removeChannel.getId() +
            " delete complete!"
    );
  }

  @DeleteMapping("/public")
  public ResponseEntity<String> deletePublicChannel(@RequestParam("id") UUID uuid) {
    Channel removeChannel = channelService.deletePublic(uuid);
    return ResponseEntity.ok(
        "Remove Public Channel ID: " + removeChannel.getId() +
            " delete complete!"
    );
  }
}
