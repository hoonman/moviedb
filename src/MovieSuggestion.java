import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "MovieSuggestion", urlPatterns = "/api/movie-suggestion")
public class MovieSuggestion extends HttpServlet {

    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;
    private final String FULLTEXTMOVIESQL = "SELECT * from movies where MATCH(title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void sendResponse(HttpServletRequest request, HttpServletResponse response, String params) throws IOException {
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource
            PreparedStatement statement = conn.prepareStatement(FULLTEXTMOVIESQL);
            // Set parameter values based on their types
            statement.setString(1, (String) params);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String title = rs.getString("title");
                String movieID = rs.getString("id");
                int year = rs.getInt("year");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", title + " ("+year+")");
                jsonObject.addProperty("data", movieID);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

    private String buildFullTextSearchMoviesQuery(String query){
        String[] prefixList = query.split(" ");
        StringBuilder sqlQuery = new StringBuilder();
        for(String prefix: prefixList){
            sqlQuery.append("+").append(prefix).append("*");
        }
        return sqlQuery.toString();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonArray jsonArray = new JsonArray();

        String query = request.getParameter("query");

        // return the empty json array if query is null or empty
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write(jsonArray.toString());
            return;
        }
        String searchQueries = buildFullTextSearchMoviesQuery(query);
        sendResponse(request, response, searchQueries);
    }


}