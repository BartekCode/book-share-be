package com.example.db.dao.role;

import com.example.core.model.role.RoleName;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class RoleDao {

    private final JdbcClient jdbcClient;

    public RoleDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    public void insertRole(String userId, RoleName role) {
        jdbcClient.sql("""
                        INSERT INTO book_share.user_role (user_id, role_value)
                        VALUES (:userId:uuid, :role)
                        ON CONFLICT (user_id, role_value) DO NOTHING;
                        """)
                .param("userId", userId)
                .param("role", role.getName())
                .update();
    }

    public void removeRole(String userId, RoleName role) {
        jdbcClient.sql("""
                                DELETE FROM book_share.user_role ur
                                WHERE ur.user_id = :userId::uuid
                                AND ur.role_value = roleName
                        """)
                .param("userId", userId)
                .param("roleName", role.getName())
                .update();
    }
}
