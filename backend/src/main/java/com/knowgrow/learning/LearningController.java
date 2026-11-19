package com.knowgrow.learning;

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
@RequestMapping("/learning")
public class LearningController {
    public record LearningDto(@NotBlank String id, @NotBlank String title, String skill,
                              @NotBlank String status, int progress, String due, String type) {}

    private static final Set<String> STATUSES = Set.of("Not Started", "In Progress", "Completed",
        "Certified", "Expired / Renewal Required");
    private final JdbcTemplate jdbc;
    public LearningController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/me")
    public List<LearningDto> mine(Authentication auth) {
        return jdbc.query("""
            SELECT id, title, skill, status, progress, due, type
            FROM learning_items WHERE user_id = ? ORDER BY title""",
            (rs, i) -> new LearningDto(rs.getString("id"), rs.getString("title"), rs.getString("skill"),
                rs.getString("status"), rs.getInt("progress"), rs.getString("due"), rs.getString("type")),
            auth.getName());
    }

    @PutMapping("/me")
    @Transactional
    public List<LearningDto> save(Authentication auth, @RequestBody List<@Valid LearningDto> items) {
        for (LearningDto l : items) {
            if (!STATUSES.contains(l.status()) || l.progress() < 0 || l.progress() > 100)
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status or progress for '" + l.title() + "'.");
        }
        jdbc.update("DELETE FROM learning_items WHERE user_id = ?", auth.getName());
        jdbc.batchUpdate("""
            INSERT INTO learning_items(user_id, id, title, skill, status, progress, due, type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""", items, items.size(), (ps, l) -> {
                ps.setString(1, auth.getName()); ps.setString(2, l.id()); ps.setString(3, l.title());
                ps.setString(4, l.skill() == null ? "" : l.skill()); ps.setString(5, l.status());
                ps.setInt(6, l.progress()); ps.setString(7, l.due() == null ? "" : l.due());
                ps.setString(8, l.type() == null ? "" : l.type());
            });
        return items;
    }
}
