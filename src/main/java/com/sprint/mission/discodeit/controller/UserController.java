package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO userDTO = userService.create(request);
        return ResponseEntity.ok(userDTO);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUser(@RequestParam("id") UUID userId) {
        UserDTO userDTO = userService.find(userId);
        return ResponseEntity.ok(userDTO);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> getAllUser() {
        List<UserDTO> all = userService.findAll();
        return ResponseEntity.ok(all);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@RequestParam("id") UUID userId, @RequestBody UpdateUserRequest request) {
        User update = userService.update(userId, request);
        return ResponseEntity.ok(update);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@RequestParam("id") UUID id) {
        UserDTO delete = userService.delete(id);
        return ResponseEntity.ok(
                "Delete user ID: " + delete.getId() +
                        " name: " + delete.getName() +
                        " delete complete!"
        );
    }
}
