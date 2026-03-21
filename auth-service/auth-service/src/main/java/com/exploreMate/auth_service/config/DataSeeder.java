package com.exploreMate.auth_service.config;

import com.exploreMate.auth_service.model.UserAccount;
import com.exploreMate.auth_service.repo.AuthRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initAdminUser(AuthRepo repo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repo.findByEmail("admin@exploremate.app").isEmpty()) {
                UserAccount admin = new UserAccount();
                admin.setName("Super Admin");
                admin.setEmail("admin@exploremate.app");
                admin.setPasswordHash(passwordEncoder.encode("Password@123"));
                admin.setRole("ADMIN");
                admin.setTitle("System Administrator");
                admin.setNumericId(System.currentTimeMillis() + (long) (Math.random() * 10000));
                
                repo.save(admin);
                System.out.println("==========================================================");
                System.out.println("DEFAULT ADMIN CREATED: admin@exploremate.app / Password@123");
                System.out.println("==========================================================");
            }
        };
    }
}
