import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet("/_dashboard/api/metadata")
public class MetadataServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE\n" +
                    "FROM information_schema.COLUMNS\n" +
                    "WHERE TABLE_SCHEMA = 'moviedb';\n";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            JsonArray metadataArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject columnMetadata = new JsonObject();
                columnMetadata.addProperty("TableName", resultSet.getString("TABLE_NAME"));
                System.out.println("table name: " + resultSet.getString("TABLE_NAME"));
                columnMetadata.addProperty("ColumnName", resultSet.getString("COLUMN_NAME"));

                System.out.println("table name: " + resultSet.getString("COLUMN_NAME"));
                columnMetadata.addProperty("DataType", resultSet.getString("DATA_TYPE"));
                System.out.println("table name: " + resultSet.getString("DATA_TYPE"));
                metadataArray.add(columnMetadata);
            }
            resultSet.close();
            out.println(metadataArray.toString());
            System.out.println(metadataArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
