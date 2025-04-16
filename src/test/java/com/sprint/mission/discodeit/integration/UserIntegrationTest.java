package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserService userService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자 생성 요청 성공 - 프로필 포함 + JSON 응답 검증")
  void createUser_withProfile_success() throws Exception {
    UserCreateRequest createRequest = new UserCreateRequest(
        "junwo",
        "junwo@email.com",
        "Password123!"
    );

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest.json",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(createRequest)
    );

    MockMultipartFile profileFile = new MockMultipartFile(
        "profile",
        "profile.png",
        "image/png",
        "fake-image-bytes".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .file(profileFile)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.username").value("junwo"))
        .andExpect(jsonPath("$.email").value("junwo@email.com"))
        .andExpect(jsonPath("$.profile.fileName").value("profile.png"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 잘못된 이메일 형식")
  void createUser_invalidEmailFormat_fail() throws Exception {
    MockMultipartFile invalidEmailRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
        {
          "username": "junwo",
          "email": "invalid-email-format",
          "password": "Password123!"
        }
        """.getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(
            multipart("/api/users")
                .file(invalidEmailRequest)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 생성 실패 - 잘못된 비밀번호 형식")
  void createUser_invalidPasswordFormat_fail() throws Exception {
    MockMultipartFile invalidPasswordRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
        {
          "username": "junwo",
          "email": "junwo@email.com",
          "password": "123"
        }
        """.getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(
            multipart("/api/users")
                .file(invalidPasswordRequest)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("모든 사용자 조회 통합 테스트")
  void findAllUsers_success() throws Exception {
      // given
    UserCreateRequest request1 = new UserCreateRequest("test1", "test1@naver.com", "Rlawnsdn12!");
    UserCreateRequest request2 = new UserCreateRequest("test2", "test2@naver.com", "Rlawnsdn12!");

    userService.create(request1, Optional.empty());
    userService.create(request2, Optional.empty());

    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username", is("test1")))
        .andExpect(jsonPath("$[0].email", is("test1@naver.com")))
        .andExpect(jsonPath("$[1].username", is("test2")))
        .andExpect(jsonPath("$[1].email", is("test2@naver.com")));
  }
}
