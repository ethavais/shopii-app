package com.example.shopii.datas;

import android.annotation.SuppressLint;
import android.util.Log;
import java.sql.*;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String IP = "10.0.2.2";
    private static final String PORT = "1433";
    private static final String DATABASE = "ShopiiDb";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "12345";

    @SuppressLint("NewApi")
    public Connection getConnection() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connString = "jdbc:jtds:sqlserver://" + IP + ":" + PORT +
                    "/" + DATABASE +
                    ";encrypt=disabled" +
                    ";loginTimeout=5" +
                    ";socketTimeout=5" +
                    ";user=" + USERNAME + 
                    ";password=" + PASSWORD;
            
            return DriverManager.getConnection(connString);
        } catch (Exception e) {
            Log.e(TAG, "Connection failed: " + e.getMessage());
            return null;
        }
    }

    public void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error closing connection: " + e.getMessage());
        }
    }
}