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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            String id2 = request.getParameter("id");

            // The log message can be found in localhost log
            request.getServletContext().log("getting id: " + id2);

//            String query = "SELECT\n" +
//                    "    sub.MovieId,\n" +
//                    "    sub.MovieTitle,\n" +
//                    "    sub.MovieYear,\n" +
//                    "    sub.Director,\n" +
//                    "    sub.Genres,\n" +
//                    "    sub.Stars,\n" +
//                    "    r.rating AS Rating\n" +
//                    "FROM\n" +
//                    "    (\n" +
//                    "        SELECT\n" +
//                    "            m.id AS MovieId,\n" +
//                    "            m.title AS MovieTitle,\n" +
//                    "            m.year AS MovieYear,\n" +
//                    "            m.director AS Director,\n" +
//                    "            (\n" +
//                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
//                    "                FROM (\n" +
//                    "                    SELECT DISTINCT g.name AS names, g.id as ids\n" +
//                    "                    FROM genres_in_movies gim\n" +
//                    "                    JOIN genres g ON gim.genreId = g.id\n" +
//                    "                    WHERE gim.movieId = m.id\n" +
//                    "                ) AS SubGenres\n" +
//                    "            ) AS Genres,\n" +
//                    "            (\n" +
//                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
//                    "                FROM (\n" +
//                    "                    SELECT DISTINCT s.name AS names, s.id as ids\n" +
//                    "                    FROM stars_in_movies sim\n" +
//                    "                    JOIN stars s ON sim.starId = s.id\n" +
//                    "                    WHERE sim.movieId = m.id\n" +
//                    "                ) AS SubStars\n" +
//                    "            ) AS Stars\n" +
//                    "        FROM\n" +
//                    "            movies m\n" +
//                    "        WHERE\n" +
//                    "            m.id = ?\n" +
//                    "    ) AS sub\n" +
//                    "JOIN ratings r ON sub.MovieId = r.movieId\n" +
//                    "ORDER BY\n" +
//                    "    r.rating DESC\n" +
//                    "LIMIT 20;\n";

            String quer2y = "SELECT\n" +
                    "    sub.MovieId,\n" +
                    "    sub.MovieTitle,\n" +
                    "    sub.MovieYear,\n" +
                    "    sub.Director,\n" +
                    "    sub.Genres,\n" +
                    "    sub.Stars,\n" +
                    "    r.rating AS Rating\n" +
                    "FROM\n" +
                    "    (\n" +
                    "        SELECT\n" +
                    "            m.id AS MovieId,\n" +
                    "            m.title AS MovieTitle,\n" +
                    "            m.year AS MovieYear,\n" +
                    "            m.director AS Director,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT DISTINCT g.name AS names, g.id as ids\n" +
                    "                    FROM genres_in_movies gim\n" +
                    "                    JOIN genres g ON gim.genreId = g.id\n" +
                    "                    WHERE gim.movieId = m.id\n" +
                    "                ) AS SubGenres\n" +
                    "            ) AS Genres,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT DISTINCT s.name AS names, s.id as ids\n" +
                    "                    FROM stars_in_movies sim\n" +
                    "                    JOIN stars s ON sim.starId = s.id\n" +
                    "                    WHERE sim.movieId = m.id\n" +
                    "                ) AS SubStars\n" +
                    "            ) AS Stars\n" +
                    "        FROM\n" +
                    "            movies m\n" +
                    "        WHERE\n" +
                    "            m.id = ?\n" +
                    "    ) AS sub\n" +
                    "JOIN ratings r ON sub.MovieId = r.movieId\n" +
                    "ORDER BY\n" +
                    "    sub.MovieTitle ASC\n" +
                    "LIMIT 20;";

            String query3 = "SELECT\n" +
                    "    sub.MovieId,\n" +
                    "    sub.MovieTitle,\n" +
                    "    sub.MovieYear,\n" +
                    "    sub.Director,\n" +
                    "    sub.Genres,\n" +
                    "    sub.Stars,\n" +
                    "    r.rating AS Rating\n" +
                    "FROM\n" +
                    "    (\n" +
                    "        SELECT\n" +
                    "            m.id AS MovieId,\n" +
                    "            m.title AS MovieTitle,\n" +
                    "            m.year AS MovieYear,\n" +
                    "            m.director AS Director,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                    "                    FROM genres_in_movies gim\n" +
                    "                    JOIN genres g ON gim.genreId = g.id\n" +
                    "                    WHERE gim.movieId = m.id\n" +
                    "                ) AS SubGenres\n" +
                    "            ) AS Genres,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT s.name AS names, s.id AS ids, COUNT(*) AS MovieCount\n" +
                    "                    FROM stars_in_movies sim\n" +
                    "                    JOIN stars s ON sim.starId = s.id\n" +
                    "                    WHERE sim.movieId = m.id\n" +
                    "                    GROUP BY s.id\n" +
                    "                    ORDER BY MovieCount DESC  -- Sort by the number of movies played in\n" +
                    "                ) AS SubStars\n" +
                    "            ) AS Stars\n" +
                    "        FROM\n" +
                    "            movies m\n" +
                    "        WHERE\n" +
                    "            m.id = ?\n" +
                    "    ) AS sub\n" +
                    "JOIN ratings r ON sub.MovieId = r.movieId\n" +
                    "ORDER BY\n" +
                    "    sub.MovieTitle ASC\n" +
                    "LIMIT 20;\n";

            String query = "SELECT\n" +
                    "    sub.MovieId,\n" +
                    "    sub.MovieTitle,\n" +
                    "    sub.MovieYear,\n" +
                    "    sub.Director,\n" +
                    "    sub.Genres,\n" +
                    "    sub.Stars,\n" +
                    "    r.rating AS Rating\n" +
                    "FROM\n" +
                    "    (\n" +
                    "        SELECT\n" +
                    "            m.id AS MovieId,\n" +
                    "            m.title AS MovieTitle,\n" +
                    "            m.year AS MovieYear,\n" +
                    "            m.director AS Director,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                    "                    FROM genres_in_movies gim\n" +
                    "                    JOIN genres g ON gim.genreId = g.id\n" +
                    "                    WHERE gim.movieId = m.id\n" +
                    "                ) AS SubGenres\n" +
                    "            ) AS Genres,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids) ORDER BY names ASC)\n" +
                    "                FROM (\n" +
                    "                    SELECT s.name AS names, s.id as ids, COUNT(sim2.starId) AS movie_count\n" +
                    "                    FROM stars AS s\n" +
                    "                    INNER JOIN stars_in_movies AS sim1 ON s.id = sim1.starId\n" +
                    "                    INNER JOIN stars_in_movies AS sim2 ON sim1.starId = sim2.starId\n" +
                    "                    WHERE sim1.movieId = m.id\n" +
                    "                    GROUP BY s.name, s.id\n" +
                    "                    ORDER BY movie_count DESC, names ASC\n" +
                    "                ) AS SubStars\n" +
                    "            ) AS Stars\n" +
                    "        FROM\n" +
                    "            movies m\n" +
                    "        WHERE\n" +
                    "            m.id = ?\n" +
                    "    ) AS sub\n" +
                    "JOIN ratings r ON sub.MovieId = r.movieId\n" +
                    "ORDER BY\n" +
                    "    (SELECT COUNT(*) FROM stars_in_movies sim WHERE sim.starId = sub.Stars) DESC, sub.MovieTitle ASC\n" +
                    "LIMIT 20;\n";

            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            statement.setString(1, id2);

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
//                jsonObject.addProperty("genres", genres);
                JsonArray genreJsonArray = new JsonArray();
                String[] genres_split = genres.split(",");
                // Iterate through the list and split each string
                for (String genre : genres_split) {
                    JsonObject genreObject = new JsonObject();
                    String[] parts = genre.split("\\|");
                    String name = parts[0];
                    String id = parts[1];
                    genreObject.addProperty("name", name);
                    genreObject.addProperty("id", id);
                    genreJsonArray.add(genreObject);
                }
                jsonObject.add("genres", genreJsonArray);

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