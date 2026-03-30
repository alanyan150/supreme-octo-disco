import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {
    private final String query;

    public Query(String query) {
        this.query = query;
    }

    public ResultSet query(Statement statement) throws SQLException {
        java.sql.ResultSet rs = null;
        try {
            System.out.println(query);
            rs = statement.executeQuery(query);
        } catch (SQLException ex) {
            int sqlCode = ex.getErrorCode(); // Get SQLCODE
            String sqlState = ex.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(ex.getMessage());
        }

        return rs;
    }
}
