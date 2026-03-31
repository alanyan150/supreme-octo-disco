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
        this.statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

    /**
     * To log in to a user profile without a password
     *
     * @param username The username of the profile
     * @return The userid of the profile
     */
    public int login(String username) {
        try {
            String tableName = "Users";
            String query = "SELECT userid FROM " + tableName + " WHERE username = '" + username + "'";
            java.sql.ResultSet rs = this.statement.executeQuery(query);
            rs.next();
            return rs.getInt("userid");
        } catch (SQLException e) {
            System.out.println("User does not exist");
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
     *
     * @param loginId  The userid of the user currently logged in
     * @param username The username of the user they want to follow
     * @param input    The scanner for the input
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
                                System.out.println("Invalid input!");
                            }
                        } while (!valid);
                    }
                } catch (SQLException e) {
                    System.out.println("User does not exist!");
                }
            }
        } catch (SQLException e) {
            sqlErrorCode(e);
        }
    }

    /**
     * Post a post
     *
     * @param loginId  The userid of the user currently logged in
     * @param caption  The caption of the post
     * @param privacy  The privacy of the post
     * @param filename The filename for the content of the post
     * @param location The location of the post
     * @param tags     The post's tags
     */
    public void q3(int loginId, String caption, String privacy, String filename, String location,
                   String tags) {
        String tableName1 = "Posts";
        String tableName2 = "Posted";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
        String query1 = "INSERT INTO " + tableName1
                + " (caption, privacy, time, fname, location, tags)"
                + " VALUES ("
                + "'" + caption + "', " + "'" + privacy + "', " + "'" + timestamp + "', "
                + "'" + filename + "', " + "'" + location + "', " + "'" + tags + "'"
                + ")";
        // atomically retrieves postid from newly posted post
        String outerQuery1 = "SELECT postid FROM FINAL TABLE" + "(" + query1 + ")";
        try {
            java.sql.ResultSet rs = this.statement.executeQuery(outerQuery1);
            if (rs.next()) {
                int postid = rs.getInt(1);
                String query2 = "INSERT INTO " + tableName2 + " VALUES (" + loginId + ", " + postid + ")";
                try {
                    this.statement.executeUpdate(query2);
                } catch (SQLException e) {
                    sqlErrorCode(e);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void q4(int loginId, Scanner input) {
        try {
            String tableName1 = "Groups";
            String tableName2 = "IsMember";
            String query1 = "SELECT gname FROM " + tableName1;
            java.sql.ResultSet rs = this.statement.executeQuery(query1);
            if (!rs.isBeforeFirst()) System.out.println("No groups found!");
            else {
                int i = 1;
                while (rs.next()) {
                    System.out.println("\t" + i++ + ". " + rs.getString(1));
                }
                System.out.println("\t" + i + ". Cancel");

                boolean valid = false;
                do {
                    System.out.print("Choose a group to join: ");
                    int choice = input.nextInt();
                    if (choice >= 1 && choice < i) {
                        for (int j = i; j != choice; j--) {
                            rs.previous();
                        }

                        String gname = rs.getString(1);

                        String tquery = "SELECT * FROM " + tableName2 + " WHERE userid = '" + loginId + "'"
                                + " AND gname = '" + gname + "'";
                        rs = this.statement.executeQuery(tquery);
                        if (rs.isBeforeFirst()) System.out.println("Already member " + gname);
                        else {
                            String query2 = "INSERT INTO " + tableName2 + " VALUES ("
                                    + loginId + ", " + "'" + gname + "')";
                            try {
                                this.statement.executeUpdate(query2);
                                System.out.println("Joined " + gname);
                            } catch (SQLException e) {
                                sqlErrorCode(e);
                            }
                        }

                        valid = true;
                    } else if (choice == i) {
                        System.out.println("Cancelled");
                        valid = true;
                    } else {
                        System.out.println("Invalid choice!");
                    }
                } while (!valid);
            }
        } catch (SQLException e) {
            sqlErrorCode(e);
        }
    }

    public void q5(int loginId, Scanner input) {
        try {
            String tableName1 = "Users";
            String tableName2 = "PrivateMessages";
            String query1 = "SELECT username FROM " + tableName1 + " WHERE userid IN ("
                    + "SELECT receiver FROM " + tableName2 + " WHERE sender = " + loginId + ")";
            java.sql.ResultSet rs = this.statement.executeQuery(query1);
            if (!rs.isBeforeFirst()) System.out.println("No users found!");
            else {
                int i = 1;
                while (rs.next()) {
                    System.out.println("\t" + i++ + ". " + rs.getString(1));
                }
                System.out.println("\t" + i + ". Cancel");
                boolean valid = false;
                do {
                    System.out.print("Choose a conversation: ");
                    int choice = input.nextInt();
                    if (choice >= 1 && choice < i) {
                        for (int j = i; j != choice; j--) {
                            rs.previous();
                        }

                        String receiver = rs.getString(1);
                        String getID = "SELECT userid FROM " + tableName1 + " WHERE username = '" + receiver + "'";
                        rs = this.statement.executeQuery(getID);
                        rs.next();
                        int ruid = rs.getInt(1);

                        String query2 = "SELECT messageid, content FROM " + tableName2 + " WHERE sender = " + loginId
                                + " AND receiver = " + ruid;
                        String query3 = "SELECT messageid, content FROM " + tableName2 + " WHERE sender = " + ruid
                                + " AND receiver = " + loginId;
                        try {
                            rs = this.statement.executeQuery(query2);
                            if (!rs.isBeforeFirst()) System.out.println("No messages found!");
                            else {
                                while (rs.next()) {
                                    System.out.println("\t" + rs.getString(2));
                                }
                            }
                        } catch (SQLException e) {
                            sqlErrorCode(e);
                        }

                        valid = true;
                    } else if (choice == i) {
                        System.out.println("Cancelled");
                        valid = true;
                    } else {
                        System.out.println("Invalid choice!");
                    }
                } while (!valid);
            }
        } catch (
                SQLException e) {
            sqlErrorCode(e);
        }
    }

    public void close() throws SQLException {
        this.statement.close();
        this.con.close();
    }
}
