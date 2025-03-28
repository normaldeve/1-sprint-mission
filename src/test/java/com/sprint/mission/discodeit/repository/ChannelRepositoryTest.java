package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("공개 타입 또는 ID가 일치하는 채널을 조회할 수 있다")
  void findAllByTypeOrIdIn() {
    // given
    Channel publicChannel1 = new Channel(ChannelType.PUBLIC, "공지사항", "전체 공지");
    Channel publicChannel2 = new Channel(ChannelType.PUBLIC, "자유채팅", "잡담방");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

    channelRepository.saveAll(List.of(publicChannel1, publicChannel2, privateChannel));

    List<UUID> ids = List.of(privateChannel.getId());

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, ids);

    // then
    assertThat(result).hasSize(3); // PUBLIC 2 + privateChannel 1
    assertThat(result).extracting(Channel::getId)
        .containsExactlyInAnyOrder(publicChannel1.getId(), publicChannel2.getId(), privateChannel.getId());
  }
}

