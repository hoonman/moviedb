import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
//        String id = request.getParameter("id");

        // The log message can be found in localhost log
//        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = " SELECT     sub.MovieId,     sub.MovieTitle,     sub.MovieYear,     sub.Director,     sub.Genres,     sub.Stars,     r.rating AS Rating FROM (     SELECT         m.id AS MovieId,         m.title AS MovieTitle,         m.year AS MovieYear,         m.director AS Director,         (SELECT GROUP_CONCAT(CONCAT(names,\"|\", ids)) FROM (SELECT DISTINCT g.name AS names, g.id as ids          FROM genres_in_movies gim          \n" +
                    "JOIN genres g ON gim.genreId = g.id          WHERE gim.movieId = m.id          LIMIT 3) AS SubGenres) AS Genres,       ( SELECT GROUP_CONCAT(CONCAT(names,\"|\",ids)) FROM ( SELECT DISTINCT s.name AS names, s.id as ids\n" +
                    "       FROM stars_in_movies  sim        JOIN stars s ON sim.starId = s.id         WHERE sim.movieId = m.id         LIMIT 3) AS SubStars) AS Stars     FROM movies m ) AS sub JOIN ratings r ON sub.MovieId = r.movieId\n" +
                    " ORDER BY r.rating DESC LIMIT 20;";
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
//            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("MovieId");
                String movieTitle = rs.getString("MovieTitle");
                String movieYear = rs.getString("MovieYear");

                String director = rs.getString("Director");
                String genres = rs.getString("Genres");
                String stars = rs.getString("Stars");


                String rating = rs.getString("Rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_Id", movieId);
                jsonObject.addProperty("movie_Title", movieTitle);
                jsonObject.addProperty("movie_Year", movieYear);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                JsonArray starsJsonArray = new JsonArray();

//                jsonObject.addP("stars", actorObject);

                String[] actors = stars.split(",");
                // Iterate through the list and split each string
                for (String actor : actors) {
                    JsonObject actorObject = new JsonObject();
                    String[] parts = actor.split("\\|");
                    String name = parts[0];
                    String id = parts[1];
                    actorObject.addProperty("name", name);
                    actorObject.addProperty("id", id);
                    starsJsonArray.add(actorObject);
                }
                jsonObject.add("stars", starsJsonArray);

                jsonObject.addProperty("rating", rating);

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

}
