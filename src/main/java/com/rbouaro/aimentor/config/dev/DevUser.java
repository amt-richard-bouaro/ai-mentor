package com.rbouaro.aimentor.config.dev;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@Profile({"dev", "default"})
@RequiredArgsConstructor
public class DevUser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DevConfigProperties devConfigProperties;

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.findByUsername(devConfigProperties.username())
                .or(() -> userRepository.findByEmail(devConfigProperties.email()))
                .ifPresentOrElse(
                        user -> log.info("Dev user already exists: {}", user.getUsername()),
                        this::createDevUser
                );
    }


    private void createDevUser() {
        log.info("Seeding new admin user: {}", devConfigProperties.username());
        User dev = User.builder()
                .username(devConfigProperties.username())
                .email(devConfigProperties.email())
                .password(passwordEncoder.encode(devConfigProperties.password()))
                .permissions(new HashSet<>(Set.of(UserPermission.USER)))
                .createdAt(LocalDateTime.now())
                .goals(new ArrayList<>())
                .build();
        userRepository.save(dev);
    }
}