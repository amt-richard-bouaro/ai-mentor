package com.rbouaro.aimentor.config.dev;

import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfigProperties adminProperties;

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.findByUsername(adminProperties.username())
                .or(() -> userRepository.findByEmail(adminProperties.email()))
                .ifPresentOrElse(
                        this::repairAdminPermissions,
                        this::createAdminUser
                );
    }

    private void createAdminUser() {
        log.info("Seeding new admin user: {}", adminProperties.username());
        User admin = User.builder()
                .username(adminProperties.username())
                .email(adminProperties.email())
                .password(passwordEncoder.encode(adminProperties.password()))
                .permissions(new HashSet<>(Set.of(UserPermission.ADMIN, UserPermission.USER)))
                .createdAt(LocalDateTime.now())
                .goals(new ArrayList<>())
                .build();
        userRepository.save(admin);
    }

    private void repairAdminPermissions(User existingUser) {
        if (!existingUser.getPermissions().contains(UserPermission.ADMIN)) {
            log.warn("User {} exists but lacks ADMIN permission. Repairing account...", existingUser.getUsername());
            existingUser.getPermissions().add(UserPermission.ADMIN);
            userRepository.save(existingUser);
        } else {
            log.debug("Admin user {} already exists and is correctly configured.", existingUser.getUsername());
        }
    }
}
