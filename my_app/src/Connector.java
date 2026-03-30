import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {
    public static void main(String[] args) throws SQLException {
        String tableName = "Users";
        int sqlCode = 0;      // Variable to hold SQLCODE
        String sqlState = "00000";  // Variable to hold SQLSTATE

        if (args.length > 0) tableName = args[0];

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

        String url = "jdbc:db2://winter2026-comp421.cs.mcgill.ca:50000/comp421";

        /*TODO
            ----------------------------REMOVE!!!----------------------------
         */
        String your_userid = "cs421g115";
        String your_password = "dblovers21";
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a username to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(url, your_userid, your_password);
        Statement statement = con.createStatement();

        try {
            String query = "SELECT * FROM " + tableName;
            Query query1 = new Query(query);
            java.sql.ResultSet rs = query1.query(statement);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                System.out.println("id: " + id + ", name: " + name);
            }
            System.out.println("DONE");
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }

        // Finally but importantly close the statement and connection
        statement.close();
        con.close();
    }

    public static void c1(String tableName, Statement statement) {
        try {
            String query = "SELECT * FROM " + tableName;
            System.out.println(query);
            java.sql.ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                System.out.println("id: " + id + ", name: " + name);
            }
            System.out.println("DONE");
        } catch (SQLException e) {
            int sqlCode = e.getErrorCode(); // Get SQLCODE
            String sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }
}
