package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("성공 - 전체 유저 조회")
  void findAll_success() throws Exception {
    UserDto user = new UserDto(UUID.randomUUID(), "rex", "rex@naver.com", null, true);
    BDDMockito.given(userService.findAll()).willReturn(List.of(user));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("rex"));
  }

  @Test
  @DisplayName("성공 - 유저 삭제")
  void deleteUser_success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/" + userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("실패 - 유효하지 않은 입력으로 유저 생성")
  void createUser_fail_invalidInput() throws Exception {
    UserCreateRequest invalidRequest = new UserCreateRequest("", "invalid", "123");

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest)
    );

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("성공 - 유저 생성")
  void createUser_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("rex", "rex@naver.com", "Password1!");
    UserDto userDto = new UserDto(UUID.randomUUID(), "rex", "rex@naver.com", null, true);

    BDDMockito.given(userService.create(any(), any())).willReturn(userDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("rex"));
  }

}

