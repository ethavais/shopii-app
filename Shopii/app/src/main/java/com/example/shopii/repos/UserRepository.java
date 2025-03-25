package com.example.shopii.repos;

import com.example.shopii.models.user.User;
import com.example.shopii.utils.DatabaseHelper;
import android.util.Log;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserRepository {
    private final DatabaseHelper dbHelper;
    private static final String TAG = "UserRepository";

    public UserRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean createUser(User user) {
        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            if (conn == null || conn.isClosed()) {
                Log.e(TAG, "No valid connection");
                return false;
            }
            
            String sql = "INSERT INTO Users (id, username, email, passwordHash, isActive) VALUES (?, ?, ?, ?, ?)";
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getId().toString());
                pstmt.setString(2, user.getUsername());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getPasswordHash());
                pstmt.setBoolean(5, user.isActive());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error [" + e.getErrorCode() + "]: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeConnection(conn);
        }
    }

    public User authenticate(String email, String password) {
        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            if (conn == null || conn.isClosed()) {
                Log.e(TAG, "No valid connection");
                return null;
            }
            
            String hashedPassword = hashPassword(password);
            
            String sql = "SELECT * FROM Users WHERE email = ? AND passwordHash = ?";
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, hashedPassword);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean isActive = rs.getBoolean("isActive");
                        if (!isActive) {
                            Log.i(TAG, "Login failed - Account is inactive: " + email);
                            return null;
                        }

                        User user = new User();
                        user.setId(UUID.fromString(rs.getString("id")));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setPasswordHash(rs.getString("passwordHash"));
                        user.setPhoneNumber(rs.getString("phoneNumber"));
                        user.setActive(isActive);
                        Log.i(TAG, "Login successful - User: " + user.getEmail());
                        return user;
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error [" + e.getErrorCode() + "]: " + e.getMessage());
            return null;
        } finally {
            dbHelper.closeConnection(conn);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte[] byteArray = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password: " + e.getMessage());
            return null;
        }
    }

    public boolean isAccountInactive(String email) {
        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            if (conn == null || conn.isClosed()) {
                Log.e(TAG, "No valid connection");
                return false;
            }
            
            String sql = "SELECT isActive FROM Users WHERE email = ?";
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return !rs.getBoolean("isActive");
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error [" + e.getErrorCode() + "]: " + e.getMessage());
            return false;
        } finally {
            dbHelper.closeConnection(conn);
        }
    }

    /**
     * Lấy thông tin người dùng theo email
     * @param email Email của người dùng cần tìm
     * @return User nếu tìm thấy, null nếu không tìm thấy
     */
    public User getUserByEmail(String email) {
        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            if (conn == null || conn.isClosed()) {
                Log.e(TAG, "No valid connection");
                return null;
            }
            
            String sql = "SELECT * FROM Users WHERE email = ?";
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setId(UUID.fromString(rs.getString("id")));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setPasswordHash(rs.getString("passwordHash"));
                        user.setPhoneNumber(rs.getString("phoneNumber"));
                        user.setActive(rs.getBoolean("isActive"));
                        Log.i(TAG, "User fetched successfully: " + user.getEmail());
                        return user;
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error [" + e.getErrorCode() + "]: " + e.getMessage());
            return null;
        } finally {
            dbHelper.closeConnection(conn);
        }
    }
}