package com.nit.placement_portal.bootstrap;

import com.nit.placement_portal.model.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.nit.placement_portal.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "app.bootstrap.admin.enabled", havingValue = "true")
public class AdminSeeder implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminSeeder(UserRepository userRepository,
                    BCryptPasswordEncoder encoder,
                    @Value("${app.bootstrap.admin.username:}") String adminUsername,
                    @Value("${app.bootstrap.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminUsername == null || adminUsername.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            logger.warn("Admin seeding skipped: set app.bootstrap.admin.username and app.bootstrap.admin.password");
            return;
        }

        if(userRepository.findByUsername(adminUsername).isEmpty()) {

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole("ADMIN");

            userRepository.save(admin);

            logger.info("Admin account created successfully");
        }
    }
}
