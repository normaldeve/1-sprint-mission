package com.sprint.mission.discodeit.security.service;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .map(UserPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
