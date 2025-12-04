package com.selarasorganizer.repository;

import com.selarasorganizer.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, email, password FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, username);
    }

    public void deleteById(Long id) {
        Long userId = jdbcTemplate.queryForObject(
            "SELECT user_id FROM asisten WHERE id = ?",
            Long.class, id
        );
    
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }
    
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}