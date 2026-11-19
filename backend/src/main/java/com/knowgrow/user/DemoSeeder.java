package com.knowgrow.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Dev convenience: the same five demo accounts as the frontend README. Turn off with SEED_DEMO_USERS=false. */
@Component
public class DemoSeeder implements ApplicationRunner {
    private final UserRepo users;
    private final PasswordEncoder encoder;
    @Value("${knowgrow.seed-demo-users}") private boolean enabled;

    public DemoSeeder(UserRepo users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) return;
        String hash = encoder.encode("demo12345");
        seed("Avery HR", "hr@knowgrow.demo", "HR Specialist", "People & Culture", "HR", "HR Specialist", hash);
        seed("Morgan Manager", "manager@knowgrow.demo", "Manager", "Engineering", "Platform", "Team Lead", hash);
        seed("Elliot Employee", "employee@knowgrow.demo", "Employee", "Engineering", "Platform", "Software Engineer", hash);
        seed("Taylor Mentor", "mentor@knowgrow.demo", "Mentor", "Engineering", "Platform", "Technical Mentor", hash);
        seed("Jordan Head", "head@knowgrow.demo", "Department Head", "Engineering", "Leadership", "Department Head", hash);
    }

    private void seed(String name, String email, String role, String dept, String team, String title, String hash) {
        if (!users.emailExists(email))
            users.create(name, email, hash, role, dept, team, title, UserRepo.initials(name));
    }
}
