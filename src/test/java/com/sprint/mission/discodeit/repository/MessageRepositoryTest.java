package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/message-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Test
  @DisplayName("채널 ID로 메시지 목록을 페이징 조회한다.")
  void findAllByChannelIdWithAuthor_success() {
    UUID channelId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Instant cursor = Instant.now().minus(15, ChronoUnit.MINUTES);
    Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channelId, cursor, pageable);

    assertThat(result).isNotEmpty();
    assertThat(result.hasNext()).isTrue();

    Message sample = result.getContent().get(0);
    assertThat(result.getContent()).hasSize(5);
    assertThat(sample.getAuthor()).isNotNull();
    assertThat(sample.getCreatedAt()).isBefore(cursor);

    List<Instant> createdAts = result.getContent().stream()
        .map(Message::getCreatedAt)
        .toList();
    List<Instant> sorted = new ArrayList<>(createdAts);
    sorted.sort(Comparator.reverseOrder());
    assertThat(createdAts).isEqualTo(sorted);
  }

  @Test
  @DisplayName("채널 ID로 가장 마지막 메시지 시간을 조회한다.")
  void findLastMessageAtByChannelId() {
    UUID channelId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    Optional<Message> message = messageRepository.findById(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0020"));
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channelId);

    assertThat(result).isPresent();
    assertThat(message.get().getCreatedAt()).isEqualTo(result.get());
  }

  @Test
  @DisplayName("채널 ID로 모든 메시지를 삭제한다.")
  void deleteAllByChannelId() {
    UUID channelId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    messageRepository.deleteAllByChannelId(channelId);

    assertThat(messageRepository.findAll()).isEmpty();
  }
}

