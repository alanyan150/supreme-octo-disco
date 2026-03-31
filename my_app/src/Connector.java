import java.sql.*;

public class Connector {
    String tableName;
    String query;
    int sqlCode = 0;      // Variable to hold SQLCODE
    String sqlState = "00000";  // Variable to hold SQLSTATE
    Connection con;
    Statement statement;

    public Connector() throws SQLException {
        this.tableName = "";
        this.query = "";
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
        this.con = DriverManager.getConnection(url, your_userid, your_password);
        this.statement = con.createStatement();
    }

    public void q1(String username) {
        try {
            String query = "SELECT * FROM " + "UserProfiles" + " WHERE username = '" + username + "'";
            //System.out.println(query);
            java.sql.ResultSet rs = this.statement.executeQuery(query);
            boolean hasdata = false;
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(3);
                String email = rs.getString(4);
                String status = rs.getString(5);
                String location = rs.getString(6);
                String joindate = rs.getString(7);
                String birthday = rs.getString(8);
                String bio = rs.getString(9);
                System.out.println(
                        "\tid: " + id
                                + "\n\tusername: " + username
                                + "\n\tname: " + name
                                + "\n\temail: " + email
                                + "\n\tstatus: " + status
                                + "\n\tlocation: " + location
                                + "\n\tjoindate: " + joindate
                                + "\n\tbirthday: " + birthday
                                + "\n\tbio: " + bio
                );
                if (!hasdata) hasdata = true;
            }
            if (!hasdata) System.out.println("\tNo user found!");
            //System.out.println("DONE");
        } catch (SQLException e) {
            int sqlCode = e.getErrorCode(); // Get SQLCODE
            String sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e.getMessage());
        }
    }

    public void close() throws SQLException {
        this.statement.close();
        this.con.close();
    }
}
