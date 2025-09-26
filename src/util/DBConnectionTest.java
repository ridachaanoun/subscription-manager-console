package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Simple connection test utility.
 * - Uses DBConnection.getConnection()
 * - Prints metadata and runs a small test query (SELECT 1)
 *
 * Usage:
 *  - Ensure DBConnection constants (JDBC_URL, JDBC_USER, JDBC_PASSWORD, JDBC_DRIVER) are set.
 *  - Run from IDE or:
 *      mvn exec:java -Dexec.mainClass="com.example.subscription.util.DBConnectionTest"
 */
public class DBConnectionTest {
    public static void main(String[] args) {
        System.out.println("Starting DB connection test...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("DBConnection.getConnection() returned null");
                System.exit(1);
            }
            if (conn.isClosed()) {
                System.err.println("Connection is closed immediately after opening");
                System.exit(1);
            }

            // Print some metadata
            System.out.println("Connected to database:");
            System.out.println("  URL: " + conn.getMetaData().getURL());
            System.out.println("  Product: " + conn.getMetaData().getDatabaseProductName()
                    + " " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("  Driver: " + conn.getMetaData().getDriverName()
                    + " " + conn.getMetaData().getDriverVersion());
            // Run a simple test query
            String testQuery = getTestQuery(conn.getMetaData().getDatabaseProductName());
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(testQuery)) {
                if (rs.next()) {
                    System.out.println("Test query result: " + rs.getObject(1));
                } else {
                    System.out.println("Test query returned no rows");
                }
            }
            System.out.println("DB connection test succeeded.");
        } catch (Exception e) {
            System.err.println("DB connection test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Return a simple portable test query. SELECT 1 works for Postgres/MySQL.
    private static String getTestQuery(String dbProductName) {
        if (dbProductName == null) return "SELECT 1";
        String n = dbProductName.toLowerCase();
        if (n.contains("mysql") || n.contains("mariadb")) return "SELECT 1";
        if (n.contains("postgresql")) return "SELECT 1";
        // fallback
        return "SELECT 1";
    }
}