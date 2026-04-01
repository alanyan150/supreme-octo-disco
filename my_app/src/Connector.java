import java.sql.*;
import java.util.Scanner;

public class Connector {
    Connection con;
    Statement statement;

    public Connector() throws SQLException {
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }
        String url = "jdbc:db2://winter2026-comp421.cs.mcgill.ca:50000/comp421";
        String your_userid = "cs421g115";
        String your_password = "dblovers21";
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error: no username."); System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error: no password."); System.exit(1);
        }
        this.con = DriverManager.getConnection(url, your_userid, your_password);
        this.statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    private void sqlError(SQLException e) {
        System.out.println("\tSQL Error [" + e.getSQLState() + "] code=" + e.getErrorCode() + ": " + e.getMessage());
    }

    /** Login by username */
    public int login(String username) {
        try (PreparedStatement ps = con.prepareStatement("SELECT userid FROM Users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("userid");
                System.out.println("User not found.");
            }
        } catch (SQLException e) { sqlError(e); }
        return -1;
    }

    /** Option 1: Look up a user profile by username */
    public void q1(String username) {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM UserProfiles WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(
                        "\tid:       " + rs.getInt("userid") +
                        "\n\tusername: " + username +
                        "\n\tname:     " + rs.getString("name") +
                        "\n\temail:    " + rs.getString("email") +
                        "\n\tstatus:   " + rs.getString("status") +
                        "\n\tlocation: " + rs.getString("location") +
                        "\n\tjoindate: " + rs.getString("joindate") +
                        "\n\tbirthday: " + rs.getString("birthday") +
                        "\n\tbio:      " + rs.getString("bio"));
                } else { System.out.println("\tUser not found: " + username); }
            }
        } catch (SQLException e) { sqlError(e); }
    }

    /** Option 2: Follow a user */
    public void q2(int loginId, String username, Scanner input) {
        if (loginId <= 0) { System.out.println("\tNot logged in."); return; }
        try (PreparedStatement ps = con.prepareStatement("SELECT userid,name FROM UserProfiles WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { System.out.println("\tUser not found: " + username); return; }
                int targetId = rs.getInt("userid");
                if (targetId == loginId) { System.out.println("\tCannot follow yourself."); return; }
                System.out.println("\tFound: userid=" + targetId + ", name=" + rs.getString("name"));
                try (PreparedStatement chk = con.prepareStatement("SELECT 1 FROM Follows WHERE follower=? AND following=?")) {
                    chk.setInt(1, loginId); chk.setInt(2, targetId);
                    try (ResultSet cr = chk.executeQuery()) {
                        if (cr.next()) { System.out.println("\tAlready following " + username); return; }
                    }
                }
                System.out.print("\tConfirm follow " + username + "? [Y/N]: ");
                String dec = input.nextLine().trim();
                if (dec.equalsIgnoreCase("Y")) {
                    try (PreparedStatement ins = con.prepareStatement("INSERT INTO Follows(follower,following) VALUES(?,?)")) {
                        ins.setInt(1, loginId); ins.setInt(2, targetId); ins.executeUpdate();
                        System.out.println("\tNow following " + username + ".");
                    }
                } else { System.out.println("\tCancelled."); }
            }
        } catch (SQLException e) { sqlError(e); }
    }

    /** Option 3: Create a post */
    public void q3(int loginId, String caption, String privacy, String filename, String location, String tags) {
        if (loginId <= 0) { System.out.println("\tNot logged in."); return; }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Posts(caption,privacy,time,fname,location,tags) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, caption); ps.setString(2, privacy);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, filename); ps.setString(5, location); ps.setString(6, tags);
            if (ps.executeUpdate() == 0) { System.out.println("\tFailed to create post."); return; }
            int postid = -1;
            try (ResultSet rs = statement.executeQuery("SELECT MAX(postid) FROM Posts")) {
                if (rs.next()) postid = rs.getInt(1);
            }
            if (postid != -1) {
                try (PreparedStatement ins = con.prepareStatement("INSERT INTO Posted(posterid,postid) VALUES(?,?)")) {
                    ins.setInt(1, loginId); ins.setInt(2, postid); ins.executeUpdate();
                    System.out.println("\tPost created! postid=" + postid);
                }
            }
        } catch (SQLException e) { sqlError(e); }
    }

    /** Option 4: Join a group — sub-menu from DB query */
    public void q4(int loginId, Scanner input) {
        if (loginId <= 0) { System.out.println("\tNot logged in."); return; }
        try {
            ResultSet rs = statement.executeQuery("SELECT gname FROM Groups ORDER BY gname");
            java.util.List<String> groups = new java.util.ArrayList<>();
            while (rs.next()) groups.add(rs.getString(1));
            if (groups.isEmpty()) { System.out.println("\tNo groups available."); return; }
            System.out.println("\tAvailable Groups:");
            for (int i = 0; i < groups.size(); i++) System.out.println("\t  " + (i+1) + ". " + groups.get(i));
            System.out.println("\t  " + (groups.size()+1) + ". Cancel");
            boolean valid = false;
            do {
                System.out.print("\tChoose: ");
                try {
                    int choice = Integer.parseInt(input.nextLine().trim());
                    if (choice >= 1 && choice <= groups.size()) {
                        String gname = groups.get(choice-1);
                        try (PreparedStatement chk = con.prepareStatement("SELECT 1 FROM IsMember WHERE userid=? AND gname=?")) {
                            chk.setInt(1,loginId); chk.setString(2,gname);
                            try (ResultSet cr = chk.executeQuery()) {
                                if (cr.next()) { System.out.println("\tAlready a member of " + gname); }
                                else {
                                    try (PreparedStatement ins = con.prepareStatement("INSERT INTO IsMember VALUES(?,?)")) {
                                        ins.setInt(1,loginId); ins.setString(2,gname); ins.executeUpdate();
                                        System.out.println("\tJoined " + gname + "!");
                                    }
                                }
                            }
                        }
                        valid = true;
                    } else if (choice == groups.size()+1) { System.out.println("\tCancelled."); valid = true; }
                    else System.out.println("\tInvalid choice.");
                } catch (NumberFormatException e) { System.out.println("\tEnter a number."); }
            } while (!valid);
        } catch (SQLException e) { sqlError(e); }
    }

    /** Option 5: View private message conversations — sub-menu from DB query */
    public void q5(int loginId, Scanner input) {
        if (loginId <= 0) { System.out.println("\tNot logged in."); return; }
        try {
            String sql = "SELECT DISTINCT u.userid,u.username FROM Users u WHERE u.userid IN ("
                    + "SELECT sender FROM PrivateMessages WHERE receiver=" + loginId
                    + " UNION SELECT receiver FROM PrivateMessages WHERE sender=" + loginId
                    + ") ORDER BY u.username";
            ResultSet rs = statement.executeQuery(sql);
            java.util.List<Integer> uids = new java.util.ArrayList<>();
            java.util.List<String> unames = new java.util.ArrayList<>();
            while (rs.next()) { uids.add(rs.getInt(1)); unames.add(rs.getString(2)); }
            if (unames.isEmpty()) { System.out.println("\tNo conversations found."); return; }
            System.out.println("\tConversations:");
            for (int i = 0; i < unames.size(); i++) System.out.println("\t  " + (i+1) + ". " + unames.get(i));
            System.out.println("\t  " + (unames.size()+1) + ". Cancel");
            boolean valid = false;
            do {
                System.out.print("\tChoose: ");
                try {
                    int choice = Integer.parseInt(input.nextLine().trim());
                    if (choice >= 1 && choice <= unames.size()) {
                        int otherId = uids.get(choice-1); String otherName = unames.get(choice-1);
                        System.out.println("\t--- Conversation with " + otherName + " ---");
                        ResultSet mRs = statement.executeQuery(
                            "SELECT sender,content,time FROM PrivateMessages WHERE "
                            + "(sender=" + loginId + " AND receiver=" + otherId + ") OR "
                            + "(sender=" + otherId + " AND receiver=" + loginId + ") ORDER BY time");
                        boolean any = false;
                        while (mRs.next()) {
                            String who = mRs.getInt("sender")==loginId ? "You" : otherName;
                            System.out.println("\t[" + mRs.getString("time") + "] " + who + ": " + mRs.getString("content"));
                            any = true;
                        }
                        if (!any) System.out.println("\tNo messages.");
                        statement.executeUpdate("UPDATE PrivateMessages SET status='RD' WHERE receiver="
                                + loginId + " AND sender=" + otherId + " AND (status='SNT' OR status IS NULL)");
                        System.out.println("\t(Messages marked as read.)");
                        valid = true;
                    } else if (choice == unames.size()+1) { System.out.println("\tCancelled."); valid = true; }
                    else System.out.println("\tInvalid choice.");
                } catch (NumberFormatException e) { System.out.println("\tEnter a number."); }
            } while (!valid);
        } catch (SQLException e) { sqlError(e); }
    }

    /** Option 6: Send a private message */
    public void q6(int loginId, String receiverUsername, String content, Scanner input) {
        if (loginId <= 0) { System.out.println("\tNot logged in."); return; }
        try (PreparedStatement ps = con.prepareStatement("SELECT userid FROM Users WHERE username=?")) {
            ps.setString(1, receiverUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { System.out.println("\tUser '" + receiverUsername + "' not found."); return; }
                int receiverId = rs.getInt("userid");
                if (receiverId == loginId) { System.out.println("\tCannot message yourself."); return; }
                int newId = 1;
                try (ResultSet idRs = statement.executeQuery("SELECT COALESCE(MAX(messageid),0)+1 FROM PrivateMessages")) {
                    if (idRs.next()) newId = idRs.getInt(1);
                }
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO PrivateMessages(messageid,sender,receiver,content,time,status) VALUES(?,?,?,?,?,'SNT')")) {
                    ins.setInt(1,newId); ins.setInt(2,loginId); ins.setInt(3,receiverId);
                    ins.setString(4,content); ins.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
                    ins.executeUpdate();
                    System.out.println("\tMessage sent to " + receiverUsername + " (msgid=" + newId + ").");
                }
            }
        } catch (SQLException e) { sqlError(e); }
    }

    public void close() throws SQLException {
        if (statement != null) statement.close();
        if (con != null) con.close();
    }
}
