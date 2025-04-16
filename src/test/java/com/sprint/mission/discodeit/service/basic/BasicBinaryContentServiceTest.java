package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@Import(BasicBinaryContentService.class)
@ExtendWith(MockitoExtension.class)
public class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  @Test
  @DisplayName("바이너리 파일 생성 성공")
  void createBinaryContent_success() {
    // given
    String fileName = "test.jpg";
    String contentType = "image/jpeg";
    byte[] bytes = "test".getBytes();
    long size = bytes.length;
    UUID id = UUID.randomUUID();

    BinaryContentCreateRequest request = new BinaryContentCreateRequest(fileName, contentType,
        bytes);
    BinaryContentDto dto = new BinaryContentDto(id, fileName, size, contentType);

    given(binaryContentRepository.save(any())).willAnswer(invocation -> {
      BinaryContent argument = invocation.getArgument(0);
      ReflectionTestUtils.setField(argument, "id", id);
      return argument;
    });

    given(binaryContentMapper.toDto(any(BinaryContent.class))).willReturn(dto);

    // when
    BinaryContentDto result = binaryContentService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    verify(binaryContentStorage).put(id, bytes);
  }

  @Test
  @DisplayName("하나의 바이너리 파일을 조회할 수 있다.")
  void findBinaryContent_success() {
    //given
    UUID id = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("test.jpg", 4L, "image/jpeg");
    ReflectionTestUtils.setField(binaryContent, "id", id);
    BinaryContentDto dto = new BinaryContentDto(id, "test.jpg", 4L, "image/jpeg");

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
    given(binaryContentMapper.toDto(any())).willReturn(dto);

    //when
    BinaryContentDto result = binaryContentService.find(id);

    //then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  @DisplayName("하나의 파일을 찾을 수 없으면 예외가 발생한다.")
  void findBinaryContent_fail() {
    // given
    UUID id = UUID.randomUUID();
    when(binaryContentRepository.findById(id)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> binaryContentService.find(id))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }

  @Test
  @DisplayName("여러 개의 바이너리 파일을 조회할 수 있다")
  void findAllByIdIn_success() {
      // given
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();

    BinaryContent content1 = new BinaryContent("file1.jpg", 10L, "image/jpeg");
    BinaryContent content2 = new BinaryContent("file2.jpg", 10L, "image/jpeg");
    ReflectionTestUtils.setField(content1, "id", id1);
    ReflectionTestUtils.setField(content2, "id", id2);

    List<BinaryContent> binaryContents = List.of(content1, content2);

    BinaryContentDto dto1 = new BinaryContentDto(id1, "file1.jpg", 10L, "image/jpeg");
    BinaryContentDto dto2 = new BinaryContentDto(id2, "file2.jpg", 10L, "image/jpeg");

    given(binaryContentRepository.findAllById(List.of(id1, id2))).willReturn(binaryContents);
    given(binaryContentMapper.toDto(content1)).willReturn(dto1);
    given(binaryContentMapper.toDto(content2)).willReturn(dto2);
      // when
    List<BinaryContentDto> binaryContentDtos = binaryContentService.findAllByIdIn(List.of(id1, id2));
      
      // then
    assertThat(binaryContentDtos.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("바이너리 파일 삭제 성공")
  void deleteBinaryContent_success() {
    // given
    UUID id = UUID.randomUUID();
    when(binaryContentRepository.existsById(id)).thenReturn(true);

    // when
    binaryContentService.delete(id);

    // then
    verify(binaryContentRepository).deleteById(id);
  }

  @Test
  @DisplayName("바이너리 파일 삭제 실패 - 존재하지 않음")
  void deleteBinaryContent_fail() {
    // given
    UUID id = UUID.randomUUID();
    when(binaryContentRepository.existsById(id)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> binaryContentService.delete(id))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }
}
