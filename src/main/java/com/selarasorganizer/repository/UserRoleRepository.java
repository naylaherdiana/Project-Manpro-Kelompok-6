package com.selarasorganizer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String findRoleByUserId(Long userId) {
        String sql = "SELECT role FROM user_roles WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, userId);
    }
}
