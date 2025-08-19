package com.rbouaro.aimentor.config.dev;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DevUser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!userRepository.existsByUsername("admin") || !userRepository.existsByEmail("admin@system.com")) {
            User user = User.builder()
                    .username("admin")
                    .email("admin@system.com")
                    .password(passwordEncoder.encode("password"))
                    .permissions(Set.of(UserPermission.USER))
                    .createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now())
                    .goals(new ArrayList<>())
                    .build();

            userRepository.save(
                    user
            );
        }


    }
}