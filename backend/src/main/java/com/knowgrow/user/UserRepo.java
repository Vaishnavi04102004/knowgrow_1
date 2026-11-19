package com.knowgrow.user;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    public static final String DEFAULT_ORG = "00000000-0000-0000-0000-000000000001";
    public record Login(UserDto user, String passwordHash, String status) {}

    private static final String COLS =
        "id, name, email, role, department, team, job_title, avatar";

    private static final RowMapper<UserDto> MAPPER = (rs, i) -> new UserDto(
        rs.getString("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"),
        rs.getString("department"), rs.getString("team"),
        rs.getString("job_title") == null ? rs.getString("role") : rs.getString("job_title"),
        rs.getString("avatar"));

    private final JdbcTemplate jdbc;
    public UserRepo(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Optional<UserDto> findById(String id) {
        return jdbc.query("SELECT " + COLS + " FROM users WHERE id = ?", MAPPER, id)
                   .stream().findFirst();
    }

    public Optional<Login> findLogin(String email) {
        return jdbc.query("SELECT " + COLS + ", password_hash, status FROM users WHERE email = ?",
            (rs, i) -> new Login(MAPPER.mapRow(rs, i), rs.getString("password_hash"), rs.getString("status")),
            email.toLowerCase()).stream().findFirst();
    }

    public boolean emailExists(String email) {
        Integer n = jdbc.queryForObject("SELECT count(*) FROM users WHERE email = ?", Integer.class, email.toLowerCase());
        return n != null && n > 0;
    }

    public void create(String name, String email, String hash, String role,
                       String department, String team, String title, String avatar) {
        jdbc.update("""
            INSERT INTO users(org_id, name, email, password_hash, role, department, team, job_title, avatar)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            DEFAULT_ORG, name, email.toLowerCase(), hash, role, department, team, title, avatar);
    }

    public static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String p : name.trim().split("\\s+")) if (!p.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(p.charAt(0)));
        return sb.toString();
    }
}
