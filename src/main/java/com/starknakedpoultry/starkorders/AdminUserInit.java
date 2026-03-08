package com.starknakedpoultry.starkorders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInit implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInit(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminUserRepository.findByUsername("starkadmin").isEmpty()) {
            AdminUser user = new AdminUser();
            user.setUsername("starkadmin");
            user.setPassword(passwordEncoder.encode("ChangeMe123!"));
            adminUserRepository.save(user);
        }
    }
}