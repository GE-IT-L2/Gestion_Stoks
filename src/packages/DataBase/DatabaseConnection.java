package packages.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL     = "jdbc:mysql://localhost:3306/gestion_stock";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection instance = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, DB_USER, DB_PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL driver not found.", e);
            }
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            try { instance.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}