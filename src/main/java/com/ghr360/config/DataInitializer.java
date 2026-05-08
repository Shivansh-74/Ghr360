package com.ghr360.config;

import com.ghr360.entity.User;
import com.ghr360.entity.UserType;
import com.ghr360.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            log.info("Default admin user already exists — skipping seed.");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@1234"))
                .firstname("Super")
                .lastname("Admin")
                .salutation("Mr.")
                .userType("ADMIN")
                .email("admin@ghr360.com")
                .phoneNo("9999999999")
                .isFirstTimeLogin(true)
                .isActive(true)
                .city("Bhopal")
                .state("Madhya Pradesh")
                .country("India")
                .build();

        userRepository.save(admin);
        log.info("================================================================");
        log.info("  Default ADMIN user created:");
        log.info("    Username : admin");
        log.info("    Password : Admin@1234");
        log.info("  Change the password after first login!");
        log.info("================================================================");
    }
}
