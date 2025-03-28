package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("채널 ID로 메시지 목록을 페이징 조회한다.")
  void findAllByChannelIdWithAuthor() {
    // given
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "test", "설명"));
    User user = userRepository.save(new User("junwo", "junwo@email.com", "pass", null));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    messageRepository.saveAll(List.of(
        new Message("Hello 1", channel, user, List.of()),
        new Message("Hello 2", channel, user, List.of())
    ));

    // when
    Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.now(),
        PageRequest.of(0, 10)
    );

    // then
    assertThat(messages).isNotNull();
    assertThat(messages.getContent()).hasSize(2);
    assertThat(messages.getContent().get(0).getAuthor()).isNotNull();
    assertThat(messages.getContent().get(0).getAuthor().getStatus()).isNotNull();
  }

  @Test
  @DisplayName("채널 ID로 가장 마지막 메시지 시간을 조회한다.")
  void findLastMessageAtByChannelId() {
    // given
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "test", "설명"));
    User user = userRepository.save(new User("junwo", "junwo@email.com", "pass", null));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    Instant now = Instant.now();
    messageRepository.save(new Message("Hi!", channel, user, List.of()));
    messageRepository.save(new Message("Latest", channel, user, List.of()));

    // when
    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(lastMessageAt).isPresent();
  }

  @Test
  @DisplayName("채널 ID로 모든 메시지를 삭제한다.")
  void deleteAllByChannelId() {
    // given
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "test", "설명"));
    User user = userRepository.save(new User("junwo", "junwo@email.com", "pass", null));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    messageRepository.saveAll(List.of(
        new Message("Msg1", channel, user, List.of()),
        new Message("Msg2", channel, user, List.of())
    ));

    // when
    messageRepository.deleteAllByChannelId(channel.getId());

    // then
    assertThat(messageRepository.findAll()).isEmpty();
  }
}

