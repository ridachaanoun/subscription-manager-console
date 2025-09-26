package util;


import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
    // --- Edit these values for your environment ---
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/subscriptions_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "1234";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";


    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found: " + JDBC_DRIVER, e);
        }
    }

    /**
     * Get a new JDBC connection using the hardcoded configuration.
     *
     * @return a new Connection
     * @throws Exception if connection fails
     */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
