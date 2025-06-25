package com.example.db.dao.user;

import com.example.core.model.role.RoleName;
import com.example.core.model.user.User;
import com.example.db.exceptions.DbException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class UserDao {

    private final JdbcClient jdbcClient;

    public UserDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public String registerUser(String username, String password, String email) {
        try {
            return jdbcClient.sql("""
                        INSERT INTO book_share.user (username, password, email, account_locked, enabled)
                        VALUES (:username, :password, :email, false, false)
                        RETURNING id;
                        """)
                    .param("username", username)
                    .param("password", password)
                    .param("email", email)
                    .query(String.class)
                    .single();
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
                throw new DbException("Username or email already exists","Login lub email istnieje już w bazie danych.", ex);
            }
            throw new DbException("Failed to register user", "Wystąpił błąd podczas rejestracji.", ex);
        }
    }

    public User getUserDataByName(String username) {
        return jdbcClient.sql("""
                        SELECT
                            u.id,
                            u.username,
                            u.email,
                            u.password,
                            u.account_locked,
                            u.enabled,
                            u.last_modified_date,
                            COALESCE(array_agg(ur.role_value), '{}') AS roles
                        FROM book_share.user u
                        JOIN book_share.user_role ur on u.id = ur.user_id
                        WHERE u.username = :username
                        GROUP BY u.id, u.username, u.email, u.password, u.account_locked, u.enabled, u.last_modified_date
                        """)
                .param("username", username)
                .query((rs, rowNum) -> {
                    String id = rs.getString("id");
                    String uname = rs.getString("username");
                    String email = rs.getString("email");
                    String pwd = rs.getString("password");
                    boolean accountLocked = rs.getBoolean("account_locked");
                    boolean enabled = rs.getBoolean("enabled");
                    LocalDateTime lastModifiedDate = rs.getTimestamp("last_modified_date") != null
                            ? rs.getTimestamp("last_modified_date").toLocalDateTime()
                            : null;
                    Object[] rolesObj = (Object[]) rs.getArray("roles").getArray();
                    String[] roles = Arrays.stream(rolesObj).map(Object::toString).toArray(String[]::new);
                    return new User(
                            id,
                            uname,
                            email,
                            pwd,
                            accountLocked,
                            enabled,
                            lastModifiedDate,
                            Arrays.stream(roles)
                                    .map(r -> RoleName.valueOf(r.toUpperCase()))
                                    .toList()
                    );
                })
                .single();
    }

    public void enableAccount(String userId, String username) {
        jdbcClient.sql("""
                        UPDATE book_share.user u
                        SET last_modified_date = NOW(), enabled = true
                        WHERE u.id = :userId::uuid
                        AND u.username = :username
                        """)
                .param("username", username)
                .param("userId", userId)
                .update();
    }
}
