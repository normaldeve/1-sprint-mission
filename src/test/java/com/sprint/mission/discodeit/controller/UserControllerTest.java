package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("사용자 생성 요청 성공 - 프로필 포함 + JSON 응답 검증")
  void createUser_withProfile_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "junwo", "junwo@email.com", null, null);

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        """
        {
          "username": "junwo",
          "email": "junwo@email.com",
          "password": "Password123!"
        }
        """.getBytes(StandardCharsets.UTF_8)
    );

    MockMultipartFile profileFile = new MockMultipartFile(
        "profile",
        "profile.png",
        "image/png",
        "fake-image-bytes".getBytes(StandardCharsets.UTF_8)
    );

    when(userService.create(any(), any())).thenReturn(userDto);

    mockMvc.perform(
            multipart("/api/users")
                .file(userCreateRequest)
                .file(profileFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("junwo"))
        .andExpect(jsonPath("$.email").value("junwo@email.com"))
        .andExpect(jsonPath("$.profile").doesNotExist())
        .andExpect(jsonPath("$.status").doesNotExist());
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
  @DisplayName("사용자 전체 조회 - JSON 응답 검증")
  void findAllUsers_success() throws Exception {
    UUID userId = UUID.randomUUID();
    when(userService.findAll()).thenReturn(List.of(
        new UserDto(userId, "junwo", "junwo@email.com", null, null)
    ));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userId.toString()))
        .andExpect(jsonPath("$[0].username").value("junwo"))
        .andExpect(jsonPath("$[0].email").value("junwo@email.com"))
        .andExpect(jsonPath("$[0].profile").doesNotExist())
        .andExpect(jsonPath("$[0].status").doesNotExist());
  }

  @Test
  @DisplayName("사용자 삭제")
  void deleteUser_success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    verify(userService).delete(userId);
  }
}