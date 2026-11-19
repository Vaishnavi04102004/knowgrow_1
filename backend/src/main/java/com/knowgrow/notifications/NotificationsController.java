package com.knowgrow.notifications;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {
    public record NoticeDto(@NotBlank String id, String title, String detail, String date, boolean read, String kind) {}
    private final JdbcTemplate jdbc;
    public NotificationsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public List<NoticeDto> mine(Authentication auth) {
        return jdbc.query("""
            SELECT id, title, detail, date, is_read, kind
            FROM notifications WHERE user_id = ? ORDER BY date DESC""",
            (rs, i) -> new NoticeDto(rs.getString("id"), rs.getString("title"), rs.getString("detail"),
                rs.getString("date"), rs.getBoolean("is_read"), rs.getString("kind")),
            auth.getName());
    }

    /** Frontend sends the full list; the server only updates read-state of the user's own notifications. */
    @PutMapping("/read-state")
    @Transactional
    public void readState(Authentication auth, @RequestBody List<@Valid NoticeDto> items) {
        jdbc.batchUpdate("UPDATE notifications SET is_read = ? WHERE user_id = ? AND id = ?",
            items, items.size(), (ps, n) -> {
                ps.setBoolean(1, n.read()); ps.setString(2, auth.getName()); ps.setString(3, n.id());
            });
    }
}
