package com.knowgrow.skills;

import com.knowgrow.common.ApiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/skills")
public class SkillsController {
    public record SkillDto(@NotBlank String id, @NotBlank String name, String category,
                           String current, @NotBlank String required, String status) {}

    private static final Set<String> LEVELS = Set.of("Unaware", "Beginner", "Intermediate", "Advanced", "Expert");
    private final JdbcTemplate jdbc;
    public SkillsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/me")
    public List<SkillDto> mine(Authentication auth) {
        return jdbc.query("""
            SELECT id, name, category, current_level, required_level, status
            FROM user_skills WHERE user_id = ? ORDER BY name""",
            (rs, i) -> new SkillDto(rs.getString("id"), rs.getString("name"), rs.getString("category"),
                rs.getString("current_level"), rs.getString("required_level"), rs.getString("status")),
            auth.getName());
    }

    @PutMapping("/me")
    @Transactional
    public List<SkillDto> save(Authentication auth, @RequestBody List<@Valid SkillDto> items) {
        for (SkillDto s : items) {
            if (!LEVELS.contains(s.required()) || (s.current() != null && !LEVELS.contains(s.current())))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid skill level for '" + s.name() + "'.");
        }
        jdbc.update("DELETE FROM user_skills WHERE user_id = ?", auth.getName());
        jdbc.batchUpdate("""
            INSERT INTO user_skills(user_id, id, name, category, current_level, required_level, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)""", items, items.size(), (ps, s) -> {
                ps.setString(1, auth.getName()); ps.setString(2, s.id()); ps.setString(3, s.name());
                ps.setString(4, s.category() == null ? "" : s.category());
                ps.setString(5, s.current()); ps.setString(6, s.required());
                ps.setString(7, s.status() == null ? "" : s.status());
            });
        return items;
    }
}
