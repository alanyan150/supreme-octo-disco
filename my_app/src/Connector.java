import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Connector {
    String query;
    Connection con;
    Statement statement;

    public Connector() throws SQLException {
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

    /**
     * Message to display if there is an SQL exception
     *
     * @param e The exception
     */
    private void sqlErrorCode(SQLException e) {
        int sqlCode = e.getErrorCode(); // Get SQLCODE
        String sqlState = e.getSQLState(); // Get SQLSTATE
        System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
        System.out.println(e.getMessage());
    }

    public int login(String username) {
        try {
            String tableName = "Users";
            String query = "SELECT userid FROM " + tableName + " WHERE username = '" + username + "'";
            java.sql.ResultSet rs = this.statement.executeQuery(query);
            rs.next();
            return rs.getInt("userid");
        } catch (SQLException e) {
            sqlErrorCode(e);
        }
        return -1;
    }

    /**
     * Query 1: Lookup a user in the database
     *
     * @param username The username of the user
     */
    public void q1(String username) {
        try {
            String tableName = "UserProfiles";
            String query = "SELECT * FROM " + tableName + " WHERE username = '" + username + "'";
            java.sql.ResultSet rs = this.statement.executeQuery(query);
            // since username if unique
            if (rs.next()) {
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
            } else {
                System.out.println("\tNo user found!");
            }
        } catch (SQLException e) {
            sqlErrorCode(e);
        }
    }

    /**
     * Follow a user
     * @param loginId The userid of the user currently logged in
     * @param username The username of the user they want to follow
     * @param input The scanner for the input
     */
    public void q2(int loginId, String username, Scanner input) {
        try {
            String tableName = "UserProfiles";
            String query = "SELECT * FROM " + tableName + " WHERE username = '" + username + "'";
            java.sql.ResultSet rs = this.statement.executeQuery(query);
            boolean hasdata = false;
            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
                String name = rs.getString(3);
                String email = rs.getString(4);
                System.out.println(
                        "\tid:" + id + ", username: " + username + ", name: " + name + ", email: " + email
                );
                hasdata = true;
            }
            if (!hasdata) System.out.println("\tNo user found!");
            else {
                tableName = "Follows";
                query = "SELECT * FROM " + tableName
                        + " WHERE follower ='" + loginId + "'" + " AND following ='" + id + "'";
                try {
                    rs = this.statement.executeQuery(query);
                    if (rs.next()) {
                        System.out.println("Already following " + username);
                    } else {
                        query = "INSERT INTO " + tableName + " VALUES (" + loginId + ", " + id + ")";
                        System.out.println("Confirm user to follow: " + username);
                        boolean valid = false;
                        do {
                            try {
                                System.out.print("[Y/N]: ");
                                String decision = input.nextLine();
                                switch (decision) {
                                    case "Y", "y":
                                        this.statement.executeUpdate(query);
                                        valid = true;
                                        break;
                                    case "N", "n":
                                        valid = true;
                                        break;
                                    default:
                                        System.out.println("Invalid input!");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("bla");
                            }
                        } while (!valid);
                    }
                } catch (SQLException e) {
                    sqlErrorCode(e);
                }
            }
        } catch (SQLException e) {
            sqlErrorCode(e);
        }
    }

    public void close() throws SQLException {
        this.statement.close();
        this.con.close();
    }
}
