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

@WebServlet(name = "SearchMoviesServlet", urlPatterns = "/api/search-movies")
public class SearchMoviesServlet extends HttpServlet {

    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
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
        System.out.println("search api doesn't work");
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String str_year = request.getParameter("year");
        String q_director = request.getParameter("director");
        String starName = request.getParameter("star_name");
        int page_number = Integer.parseInt(request.getParameter("page_number")) - 1;
        int page_size = Integer.parseInt(request.getParameter("page_size"));

        title = isNullOrEmpty(title) ? "!" : title;
        int year = isNullOrEmpty(str_year) ? -1 : Integer.parseInt(str_year);
        q_director = isNullOrEmpty(q_director) ? "!" : q_director;
        starName = isNullOrEmpty(starName) ? "!" : starName;
        System.out.println("title: "+title+ "year" + year + "q_director" + q_director + "starName"+starName);

        // The log message can be found in localhost log
        request.getServletContext().log("getting title: " + title);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
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
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                    "                FROM\n" +
                    "                    (\n" +
                    "                        SELECT DISTINCT g.name AS names, g.id AS ids\n" +
                    "                        FROM genres_in_movies gim\n" +
                    "                        JOIN genres g ON gim.genreId = g.id\n" +
                    "                        WHERE gim.movieId = m.id\n" +
                    "                        LIMIT 3\n" +
                    "                    ) AS SubGenres\n" +
                    "            ) AS Genres,\n" +
                    "            (\n" +
                    "                SELECT GROUP_CONCAT(CONCAT(names, \"|\", ids))\n" +
                    "                FROM\n" +
                    "                    (\n" +
                    "                        SELECT DISTINCT s.name AS names, s.id AS ids\n" +
                    "                        FROM stars_in_movies sim\n" +
                    "                        JOIN stars s ON sim.starId = s.id\n" +
                    "                        WHERE sim.movieId = m.id\n" +
//                    "                        AND\n" +
//                    "                        (? = \'!\' OR s.name LIKE ?)\n" +
                    "                        LIMIT 3\n" +
                    "                    ) AS SubStars\n" +
                    "            ) AS Stars\n" +
                    "        FROM\n" +
                    "            movies m\n" +
                    "        WHERE " +
                    "           (? = \'!\' OR m.director LIKE ?)\n" +
                    "           AND (? = -1 OR m.year = ?)\n" +
                    "           AND (? = \'!\'  OR m.title LIKE ?)\n" +
                    "    ) AS sub\n" +
                    "JOIN ratings r ON sub.MovieId = r.movieId\n" +
                    "WHERE\n" +
                    "    sub.MovieId IN (\n" +
                    "        SELECT movieId\n" +
                    "        FROM stars_in_movies\n" +
                    "        WHERE starId IN (\n" +
                    "            SELECT id\n" +
                    "            FROM stars\n" +
                    "            WHERE (name LIKE ? OR ? = '!')\n" +
                    "        )\n" +
                    "    )\n"+
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT ?,?;\n";
            PreparedStatement statement = conn.prepareStatement(query);
            System.out.println("query: "+ statement.toString());
            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            statement.setString(1, q_director);
            statement.setString(2, "%"+q_director+"%");
            statement.setInt(3, year);
            statement.setInt(4, year);
            statement.setString(5, title);
            statement.setString(6, "%"+ title+"%");
            statement.setString(7, "%" +starName+"%");
            statement.setString(8, starName);

            statement.setInt(9, page_number * page_size);
            statement.setInt(10, page_size);

            System.out.println("query: "+ statement.toString());

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

                JsonArray genreJsonArray = new JsonArray();
                String[] genres_split = genres.split(",");
                // Iterate through the list and split each string
                for (String genre : genres_split) {
                    JsonObject actorObject = new JsonObject();
                    String[] parts = genre.split("\\|");
                    String name = parts[0];
                    String id = parts[1];
                    actorObject.addProperty("name", name);
                    actorObject.addProperty("id", id);
                    genreJsonArray.add(actorObject);
                }
                jsonObject.add("genres", genreJsonArray);

                JsonArray starsJsonArray = new JsonArray();

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
